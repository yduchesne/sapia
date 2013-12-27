package org.sapia.regis.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Query;
import org.sapia.regis.RWNode;
import org.sapia.regis.util.CompositeHashMap;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

public class NodeImpl implements RWNode, Serializable{

  static final long serialVersionUID = 1L;
  
  private Long id;
  private boolean inheritsParent;
  private int version;
  private Date createDate = new Date();
  private Date modifDate = new Date();
  private String editUser;
  private String name = ROOT_NAME;
  private String type;
  private NodeImpl parent;
  private Map valueMap = new TreeMap();
  private Map childrenMap = new TreeMap();
  private List appendedLinks = new ArrayList();
  private List prependedLinks = new ArrayList();
  private List includes = new ArrayList();  
  private boolean render = true;
  
  static final NullContext NULL_CONTEXT = new NullContext();
  
  public NodeImpl(){
    this(true);
  }
  public NodeImpl(boolean render){
    this.render = render;
  }
  
  public NodeImpl(NodeImpl parent, String name, boolean render){
    this.parent = parent;
    this.name = name;
    if(name.equals(ROOT_NAME) && parent != null){
      throw new IllegalStateException("Name of child node cannot have null or empty name");
    }
    this.render = render;
  }
  
  public String getName(){
    return this.name;
  }
  
  public Collection getChildrenNames() {
    TreeSet names = new TreeSet();
    Iterator itr = this.childrenMap.values().iterator();
    while(itr.hasNext()){
      Node child = (Node)itr.next();
      names.add(child.getName());
    }
    return names;
  }
  
  public String getType(){
    return this.type;
  }
  
  public void setType(String type){
    this.type = type;
  }
  
  public Path getAbsolutePath() {
    List tokens = new ArrayList();
    doGetPath(tokens);
    return new Path(tokens);
  }
  public long lastModifChecksum() {
    Checksum chk = new Checksum();
    doCalculateChecksum(chk);
    return chk.result();
  }
  
  public Node getParent(){
    return this.parent;
  }
  
  public boolean isRoot(){
    return getParent() == null;
  }
  
  public Map getProperties() {
    return doGetProperties(Collections.EMPTY_MAP);
  }
  
  public Map getProperties(Map values) {
    return doGetProperties(values);
  }
  
  public Collection getPropertyKeys() {
    List keys = new ArrayList();
    doGetPropertyKeys(keys);
    return keys;
  }  
  
  public Node createChild(String name) throws DuplicateNodeException{
    if(getChildrenMap().get(name) != null){
      throw new DuplicateNodeException(name);
    }
    if(name.equals(Node.ROOT_NAME)){
      throw new IllegalStateException("Empty name is invalid");
    }
    NodeImpl node = new NodeImpl(this, name, render);
    getChildrenMap().put(name, node);
    return node;
  }
  
  public Node getChild(String name){
    if(name.equals(ROOT_NAME)){
      return this;
    }
    Node node = (Node)getChildrenMap().get(name);
    if(node == null){
      Iterator itr = getIncludes().iterator();
      while(itr.hasNext()){
        node = (Node)itr.next();
        if(node.getName().equals(name)){
          return node;
        }
      }
      return null;
    }
    else{
      return node;
    }
  }
  
  public Node getChild(Path path){
    return doGetChild(path.tokens());
  }
  
  public void deleteChild(String name) {
    getChildrenMap().remove(name);
  }
  
  public void deleteChildren() {
    getChildrenMap().clear();
  }
  
  public Collection getChildren(){
    Collection children = getChildrenMap().values();
    List toReturn = new ArrayList(children);
    toReturn.addAll(this.getIncludes());
    return toReturn;
  }
  
  public void setProperty(String key, String value) {
    getValueMap().put(key, value);
  }
  
  public Property getProperty(String key) {
    
    Property prop = doGetLinkedProperty(getPrependedLinks(), key);
    if(prop == null || prop.isNull()){
      String value = (String)getValueMap().get(key);
      if(value == null && isInheritsParent() && getParent() != null){
        prop = getParent().getProperty(key);
        if(!prop.isNull()) return prop;
      }
      else if(value != null){
        return new PropertyImpl(key, value);
      }
      prop = doGetLinkedProperty(getAppendedLinks(), key);
      if(prop == null){
        prop = new PropertyImpl(key, value);
      }
    }
    return prop;
  }
  
  public Property renderProperty(String key){
    return renderProperty(key, Collections.EMPTY_MAP);
  }
  
  public Property renderProperty(String key, Map values) {
    Property value = getProperty(key);
    if(value.isNull()){
      return value;
    }
    doRenderProperty((PropertyImpl)value, values);
    return value;
  }
  
  public void deleteProperties() {
    getValueMap().clear();
  }
  
  public void deleteProperty(String key) {
    getValueMap().remove(key);
  }
  
  public boolean isInheritsParent() {
    return inheritsParent;
  }

  public void setInheritsParent(boolean inheritsParent) {
    this.inheritsParent = inheritsParent;
  }  
  
  public void appendLink(Node node) {
    getAppendedLinks().add(node);
  }
  
  public void removeAppendedLink(Node node) {
    getAppendedLinks().remove(node);
  }
  
  public void prependLink(Node node) {
    getPrependedLinks().add(node);
  }
  
  public void removePrependedLink(Node node) {
    getPrependedLinks().remove(node);
  }
  
  public void addInclude(Node node) {
    getIncludes().add(node);
  }
  
  public void removeInclude(Node node) {
    getIncludes().remove(node);
  }
  
  public Collection getLinks(boolean prepended) {
    if(prepended){
      return new ArrayList(getPrependedLinks());
    }
    else{
      return new ArrayList(getPrependedLinks());
    }
  }
  
  public void deleteLinks() {
    getPrependedLinks().clear();
    getAppendedLinks().clear();
  }
  
  public void moveTo(Node newParent) {
    if(isRoot()){
      throw new IllegalStateException("Cannot move root under another node");
    }
    if(newParent.getChild(getName()) != null){
      throw new IllegalStateException("Node already exists for name: " + getName());
    }
    NodeImpl impl = (NodeImpl)newParent;
    setParent(impl);
    impl.getChildrenMap().put(getName(), this);
  }
  
  public Map getChildrenMap() {
    return childrenMap;
  }

  public void setChildrenMap(Map childrenMap) {
    this.childrenMap = childrenMap;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public String getEditUser() {
    return editUser;
  }

  public void setEditUser(String editUser) {
    this.editUser = editUser;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getModifDate() {
    return modifDate;
  }

  public void setModifDate(Date modifDate) {
    this.modifDate = modifDate;
  }

  public Map getValueMap() {
    return valueMap;
  }

  public void setValueMap(Map valueMap) {
    this.valueMap = valueMap;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParent(NodeImpl parent) {
    this.parent = parent;
  }
  
  public List getAppendedLinks() {
    return appendedLinks;
  }

  public void setAppendedLinks(List appendedLinks) {
    this.appendedLinks = appendedLinks;
  }

  public List getPrependedLinks() {
    return prependedLinks;
  }

  public void setPrependedLinks(List prependedLinks) {
    this.prependedLinks = prependedLinks;
  }

  public void setIncludes(List includes) {
    this.includes = includes;
  }  
  
  public Collection getIncludes() {
    return includes;
  }
  
  public void deleteIncludes() {
    getIncludes().clear();
  }
  
  public Collection getNodes(Query query) {
    Path p = query.getPath();
    if(p != null){
      Node node = getChild(p);
      if(node == null){
        return Collections.EMPTY_LIST;
      }
      else{
        return ((NodeImpl)node).doGetNodes(query);
      }
    }
    else{
      return doGetNodes(query);
    }
  }
  
  public void setRender(boolean render) {
    this.render = render;
  }
  
  public boolean isRender() {
    return render;
  }
    
  public int hashCode(){
    if(getId() == null) return super.hashCode(); 
    return getId().hashCode();
  }
  
  public boolean equals(Object other){
    if(other instanceof NodeImpl){
      NodeImpl node = (NodeImpl)other;
      if(getId() == null || node.getId() == null){
        return super.equals(other);
      }
      else{
        return getId().equals(node.getId());  
      }
    }
    else{
      return false;
    }
  }
  
  protected Node doGetChild(Iterator itr){
    if(itr.hasNext()){
      String name = (String)itr.next();
      NodeImpl child = (NodeImpl)getChildrenMap().get(name);
      if(child != null){
        return child.doGetChild(itr);
      }
      else{
        Iterator includes = getIncludes().iterator();
        while(includes.hasNext()){
          child = (NodeImpl)includes.next();
          if(child.getName().equals(name)){
            return child;
          }
        }
        return null;        
      }
    }
    return this;
  }
  
  protected Map doGetProperties(Map values){
    CompositeHashMap fill = new CompositeHashMap();
    fill.addChild(values);
    fill.putAll(getValueMap());
    if(isInheritsParent() && getParent() != null){
      Map props = ((NodeImpl)getParent()).doGetProperties(values);
      doRender(fill, props);
    }
    
    List links = getPrependedLinks();
    for(int i = 0; i < links.size(); i++){
      NodeImpl node = (NodeImpl)links.get(i);
      doRender(fill, node.doGetProperties(values));
    }
    
    links = getAppendedLinks();
    for(int i = 0; i < links.size(); i++){
      NodeImpl node = (NodeImpl)links.get(i);
      Map appended = node.getProperties();
      doRender(appended, fill);
      fill.putAll(appended);
    }
    
    fill.putAll(getValueMap());    
    
    doRender(fill, fill);
    
    return fill;
  }
  
  
  protected void doRender(Map target, Map src){
    if(render){
      TemplateFactory fac = new TemplateFactory();
      fac.setThrowExcOnMissingVar(false);
      TemplateContextIF ctx = new MapContext(src, NULL_CONTEXT, false);
      
      Iterator keys = target.keySet().iterator();
      while(keys.hasNext()){
        String key = (String)keys.next();
        String val = (String)target.get(key);
        if(val != null){
          try{
            val = fac.parse(val).render(ctx);
            target.put(key, val);        
          }catch(TemplateException e){}      
        }
      }
      
      keys = src.keySet().iterator();
      while(keys.hasNext()){
        String key = (String)keys.next();
        String val = (String)src.get(key);
        if(!target.containsKey(key) && val != null){
          target.put(key, val);    
        }
      }    
    }
  }
  
  protected void doRenderProperty(PropertyImpl prop, Map values){
    if(render){
      if(isInheritsParent() && getParent() != null){
        ((NodeImpl)getParent()).doRenderProperty(prop, values);
      }
      for(int i = 0; i < prependedLinks.size(); i++){
        NodeImpl node = (NodeImpl)prependedLinks.get(i);
        node.doRenderProperty(prop, values);
      }
      
      TemplateFactory fac = new TemplateFactory();
      fac.setThrowExcOnMissingVar(false);
      Map vars = new CompositeHashMap().addChild(values);
      vars.putAll(getValueMap());
      TemplateContextIF ctx = new MapContext(vars, NULL_CONTEXT, false);
      try{
        prop._value = fac.parse(prop._value).render(ctx);
      }catch(TemplateException e){}
      
      for(int i = 0; i < appendedLinks.size(); i++){
        NodeImpl node = (NodeImpl)appendedLinks.get(i);
        node.doRenderProperty(prop, values);
      }
  
      if (prop != null && prop._value.contains(TemplateFactory.DEFAULT_STARTING_DELIMITER)) {
        try{
          prop._value = fac.parse(prop._value).render(ctx);
        }catch(TemplateException e){}
      }
    }
  }  
  
  protected void doGetPropertyKeys(List fill){
    if(isInheritsParent() && getParent() != null){
      ((NodeImpl)getParent()).doGetPropertyKeys(fill);
    }
    for(int i = 0; i < prependedLinks.size(); i++){
      NodeImpl node = (NodeImpl)prependedLinks.get(i);
      node.doGetPropertyKeys(fill);
    }
    for(int i = 0; i < appendedLinks.size(); i++){
      NodeImpl node = (NodeImpl)appendedLinks.get(i);
      node.doGetPropertyKeys(fill);
    }    
    Iterator keys = getValueMap().keySet().iterator();
    while(keys.hasNext()){
      String key = (String)keys.next();
      fill.add(key);
    }
  }  
  
  protected void doGetPath(List tokens){
    if(getParent() != null){
      ((NodeImpl)getParent()).doGetPath(tokens);
    }
    String name = getName();
    if(name == null || name.equals(ROOT_NAME)){
      return;
    }
    tokens.add(getName());
  }
  
  protected void doCalculateChecksum(Checksum chk){
    chk.calc(getVersion());
    if(isInheritsParent() && getParent() != null){
      ((NodeImpl)getParent()).doCalculateChecksum(chk);
    }
    for(int i = 0; i < prependedLinks.size(); i++){
      NodeImpl node = (NodeImpl)prependedLinks.get(i);
      node.doCalculateChecksum(chk);
    }
    for(int i = 0; i < appendedLinks.size(); i++){
      NodeImpl node = (NodeImpl)appendedLinks.get(i);
      node.doCalculateChecksum(chk);
    }
    for(int i = 0; i < includes.size(); i++){
      NodeImpl node = (NodeImpl)includes.get(i);
      node.doCalculateChecksum(chk);
    }        
  }
  
  
  protected Property doGetLinkedProperty(List links, String key){
    Property prop = null;
    for(int i = 0; i < links.size(); i++){
      Node link = (Node)links.get(i);
      prop = link.getProperty(key);
      if(!prop.isNull()){
        break;
      }
    }    
    return prop;
  }
  
  private static final class Checksum{
    private long result;
    
    Checksum(){
    }
    
    long result(){
      return result;
    }
    
    void calc(long version){
       result = result ^ version;
    }
  }
  
  protected Collection doGetNodes(Query query){
    Map crit = query.getCriteria();
    List result = new LinkedList();
    Iterator children = getChildren().iterator();
    while(children.hasNext()){
      Node child = (Node)children.next();
      boolean matches = true;
      Iterator propNames = crit.keySet().iterator();      
      while(propNames.hasNext()){
        String name = (String)propNames.next();
        String val = (String)crit.get(name);
        Property prop = child.getProperty(name);
        if(prop.isNull()){
          matches = false;
          break;
        }
        else if(prop.getValue().equals(val)){
          continue;
        }
        else{
          matches = false;
          break;
        }
      }
      if(matches){
        result.add(child);
      }
    }
    return result;
  }
  
  public static final class NullContext implements TemplateContextIF, Serializable{
    
    static final long serialVersionUID = 1L;
    
    public Object getValue(String key) {
      return new StringBuffer().append("${")
          .append(key).append("}").toString();
    }
    
    public void put(String arg0, Object arg1) {
    }
  }

}
