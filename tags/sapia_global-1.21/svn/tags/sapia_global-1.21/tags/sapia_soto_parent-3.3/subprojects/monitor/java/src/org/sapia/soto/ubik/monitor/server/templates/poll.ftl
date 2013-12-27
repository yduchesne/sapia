<results errorCount="${Model.errorCount}" resultCount="${Model.resultCount}">
  <#list Model.results as result>
  <result status="<#if result.error>500<#else>200</#if>" <#if result.serviceId?exists>id="${result.serviceId}"</#if> <#if result.serviceClassName?exists>class="${result.serviceClassName}"</#if>>
    <#if result.error && result.getError()?exists>
    <error>
    ${result.getError().message?default("Unavailable error message")}
    </error>
    </#if>
    <#if result.properties?exists>
    <properties count="${result.properties?size}">
      <#assign keys = result.properties?keys >
      <#list keys as key>
      <property name="${key}" value="${result.properties[key]}" />
      </#list>
    </properties>
    <#else>
    <properties count="0">
    </properties>    
    </#if>
  </result>
  </#list>
</results>
