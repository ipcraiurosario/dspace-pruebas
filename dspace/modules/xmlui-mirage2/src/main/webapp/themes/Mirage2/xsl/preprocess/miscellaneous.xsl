<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->
<xsl:stylesheet
        xmlns="http://di.tamu.edu/DRI/1.0/"
        xmlns:dri="http://di.tamu.edu/DRI/1.0/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
        xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
        exclude-result-prefixes="xsl dri i18n">

	<xsl:output indent="yes"/>

	<!-- Add a big limit for DDC choices lookup params, in order to display all posible values at once... -->
	<xsl:template match="dri:field[@id='aspect.submission.StepTransformer.field.dc_subject_ddc']/dri:params[@choicesPresentation='lookup']">
        <params>
            <xsl:call-template name="copy-attributes"/>
            <xsl:attribute name="choicesLimit">
                <xsl:text>5000</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="isReadOnly">
                <xsl:text>true</xsl:text>
            </xsl:attribute>
        </params>
    </xsl:template>

</xsl:stylesheet>