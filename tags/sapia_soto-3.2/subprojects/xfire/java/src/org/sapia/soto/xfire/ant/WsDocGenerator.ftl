<service>
  <documentation>
    <#if WebService.javaClass.comment?exists>
    ${WebService.javaClass.comment}
    </#if>
  </documentation>
  
  <!-- METHODS -->
  <#list WebService.methods as methodModel>
  
  <method name="${methodModel.operationName}" parametersNumber="${methodModel.paramCount}">
    <documentation>
	  <#if methodModel.javaMethod.comment?exists>
      ${methodModel.javaMethod.comment}
	  </#if>        
    </documentation>
    
    <#list methodModel.parameters as paramModel>
    <parameter index="${paramModel.index}">
      <documentation>
        <#if paramModel.docTag?exists && paramModel.docTag.value?exists>
        ${paramModel.doc}
        </#if>          
      </documentation>
    </parameter>
    </#list>
    
    <#assign returnModel = methodModel.return>
    <return>
      <documentation>
        <#if returnModel.isVoid()>
        ---
  	    <#elseif returnModel.docTag?exists && returnModel.docTag.value?exists>
        ${returnModel.docTag.value}
	    </#if>
      </documentation>	
    </return>
    
    <#list methodModel.exceptions as exceptionModel>    
    <exception class="${exceptionModel.javaClass.fullyQualifiedName}">
      <documentation>
  	    <#if exceptionModel.docTag?exists && exceptionModel.docTag.value?exists>
        ${exceptionModel.doc}
	    </#if>      
      </documentation>
    </exception>
    </#list>
  </method>
  
</#list>
  <#-- method name="findBook" parametersNumber="1" >
     <documentation>
        Finds book by isbn
     </documentation>
     <parameter index="0">
        <documentation>
           ISBN number
        </documentation>
        </parameter>
        
      <return>
        <documentation>
         Book with given isbn number
        </documentation>
      </return>
      
      <exception class="org.codehaus.xfire.demo.BookException">
        <documentation>
         Thrown when no book is found for given isbn number.
        </documentation>
      </exception>
      
  </method>
  
    <method name="getBooks" parametersNumber="0" >
     <documentation>
        Get all books
     </documentation>
        
      <return>
        <documentation>
         All books.
        </documentation>
      </return>
  </method-->
</service>