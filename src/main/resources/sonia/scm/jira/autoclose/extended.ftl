Issue closed by changeset ${changeset.id} of repository ${repository.name}.

<#if diffUrl??>
Changes: ${diffUrl}
</#if>
<#if diffRestUrl??>
Patch: ${diffRestUrl}
</#if>
Changeset:
<#if changeset.description??>
${changeset.description}
</#if>


<#if changeset.modifications??>
Modified Files:
<#if changeset.modifications.added??>
<#list changeset.modifications.added as add>
  A ${add}
</#list>
</#if>

<#if changeset.modifications.modified??>
<#list changeset.modifications.modified as mod>
  M ${mod}
</#list>
</#if>

<#if changeset.modifications.removed??>
<#list changeset.modifications.removed as rm>
  R ${rm}
</#list>
</#if>
</#if>
