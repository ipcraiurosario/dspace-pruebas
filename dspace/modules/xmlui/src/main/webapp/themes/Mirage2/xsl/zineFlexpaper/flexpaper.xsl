<?xml version="1.0" encoding="UTF-8"?>
<!-- The contents of this file are subject to the license and copyright detailed 
	in the LICENSE and NOTICE files at the root of the source tree and available 
	online at http://www.dspace.org/license/ -->
<!-- TODO: Describe this XSL file Author: Alexey Maslov -->

<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:xlink="http://www.w3.org/TR/xlink/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="i18n dri mets xlink xsl dim xhtml mods dc"
	xmlns:confman="org.dspace.core.ConfigurationManager" 
	xmlns:flexUtils="es.arvo.app.mediafilter.FlexpaperUtils">

	<xsl:output indent="yes" />

    <xsl:template match="dri:div[@id='aspect.artifactbrowser.FlexpaperViewer.div.flexpaper']">
    	<xsl:call-template name="flexpaperViewer">
    		 <xsl:with-param name="flashPath" select="//dri:field[@id='aspect.artifactbrowser.FlexpaperViewer.field.flashPath']/dri:value"/> 
    		 <xsl:with-param name="split" select="//dri:field[@id='aspect.artifactbrowser.FlexpaperViewer.field.split']/dri:value"/> 
    		 <xsl:with-param name="numPages" select="//dri:field[@id='aspect.artifactbrowser.FlexpaperViewer.field.numPages']/dri:value"/>
    	</xsl:call-template>
    </xsl:template>
    
    <xsl:template name="flexpaperPreviewer">
     	<xsl:param name="flashPath"/>
     	<xsl:param name="bitstreamData" select="flexUtils:getBitstreamData($flashPath)"/>

     	<xsl:choose>
			<xsl:when test="string-length($bitstreamData)=0">
				<i18n:text>xmlui.item-view.previsualizar.error</i18n:text>
			</xsl:when>
			<xsl:otherwise>
		     	<xsl:call-template name="flexpaperViewer">
		    		 <xsl:with-param name="flashPath" select="flexUtils:tokenizePos($bitstreamData,0,',')"/> 
		    		 <xsl:with-param name="split" select="flexUtils:tokenizePos($bitstreamData,1,',')"/> 
		    		 <xsl:with-param name="numPages" select="flexUtils:tokenizePos($bitstreamData,2,',')"/> 
		    	</xsl:call-template>
	    	</xsl:otherwise>
    	</xsl:choose>
    </xsl:template>
    
    <xsl:template name="flexpaperViewer">
	    <xsl:param name="flashPath"/>
	    <xsl:param name="split"/>
	    <xsl:param name="numPages"/>
	<!--     Variables
	    <xsl:value-of select="$flashPath"/>-
	    <xsl:value-of select="$split"/>-
	    <xsl:value-of select="$numPages"/>-
	    Fin Variables -->
    <script type="text/javascript">
     <xsl:attribute name="src">
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
         <xsl:text>/themes/</xsl:text>
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='theme'][@qualifier='path']"/>
         <xsl:text>/xsl/zineFlexpaper/js/jquery.min.js</xsl:text>
     </xsl:attribute>&#160;</script>
        <script type="text/javascript">
     <xsl:attribute name="src">
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
         <xsl:text>/themes/</xsl:text>
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='theme'][@qualifier='path']"/>
         <xsl:text>/xsl/zineFlexpaper/js/jquery.extensions.min.js</xsl:text>
     </xsl:attribute>&#160;</script>
    <script type="text/javascript">
     <xsl:attribute name="src">
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
         <xsl:text>/themes/</xsl:text>
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='theme'][@qualifier='path']"/>
         <xsl:text>/xsl/zineFlexpaper/js/flexpaper.js</xsl:text>
     </xsl:attribute>&#160;</script>
	 <script type="text/javascript">
     <xsl:attribute name="src">
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
         <xsl:text>/themes/</xsl:text>
         <xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='theme'][@qualifier='path']"/>
         <xsl:text>/xsl/zineFlexpaper/js/flexpaper_handlers.js</xsl:text>
     </xsl:attribute>&#160;</script>
    	<!-- <span class="bold">
			<i18n:text>xmlui.dri2xhtml.METS-1.0.item-preview</i18n:text>
		</span>-->

		<xsl:attribute name="href">
			<xsl:value-of select="@OBJID" />
		</xsl:attribute>
				
		<div id="documentViewer" class="flexpaper_viewer" style="position:relative;width:100%;height:480px;background-color:#444444">-</div>
		<script type="text/javascript">


	    var startDocument = "Paper";

	    $('#documentViewer').FlexPaperViewer(
            { config : {
<!-- Si hay split -->
<xsl:if test="$split='split'">
   SWFFile : '{<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                <xsl:value-of select="$flashPath"/><xsl:text disable-output-escaping="yes">&amp;fileType=swf&amp;page=[*,0]</xsl:text>,<xsl:value-of select="$numPages"/>}',
   JSONFile : '<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                <xsl:value-of select="$flashPath"/><xsl:text disable-output-escaping="yes">&amp;fileType=json&amp;page={page}</xsl:text>',
   IMGFiles : '<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                <xsl:value-of select="$flashPath"/><xsl:text disable-output-escaping="yes">&amp;fileType=png&amp;page={page}</xsl:text>',
       
 </xsl:if>
<!-- Si no hay split -->
 <xsl:if test="$split='full'">
   SWFFile : '<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                <xsl:value-of select="$flashPath"/><xsl:text disable-output-escaping="yes">&amp;fileType=swf</xsl:text>',
   JSONFile : '<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                <xsl:value-of select="$flashPath"/><xsl:text disable-output-escaping="yes">&amp;fileType=json</xsl:text>',
   IMGFiles : '<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                <xsl:value-of select="$flashPath"/><xsl:text disable-output-escaping="yes">&amp;fileType=png&amp;page=1</xsl:text>',
 </xsl:if>

                
				jsDirectory:'<xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/><xsl:text>/themes/</xsl:text><xsl:value-of select="$documentRoot/dri:meta/dri:pageMeta/dri:metadata[@element='theme'][@qualifier='path']"/>
         <xsl:text>/xsl/zineFlexpaper/js/</xsl:text>',
				
                Scale : 0.6,
                ZoomTransition : 'easeOut',
                ZoomTime : 0.5,
                ZoomInterval : 0.2,
                FitPageOnLoad : true,
                FitWidthOnLoad : true,
                FullScreenAsMaxWindow : false,
                ProgressiveLoading : false,
                MinZoomSize : 0.2,
                MaxZoomSize : 5,
                SearchMatchAll : false,
               	<xsl:if test="$numPages=1">
                	InitViewMode : 'Portrait',
                </xsl:if>
                <xsl:if test="$numPages>1">
                	InitViewMode : 'TwoPage',
                </xsl:if>
           
                RenderingOrder : 'html,flash',
                StartAtPage : '',
				key : "@c225ab2176e08485388$5ab1100f4bedb009459",
                ViewModeToolsVisible : true,
                ZoomToolsVisible : true,
                NavToolsVisible : true,
                CursorToolsVisible : true,
                SearchToolsVisible : true,
                WMode : 'transparent',
                localeChain: 'es_ES'
            }}
    );
	</script>
        </xsl:template>  
    
	
	<xsl:template name="string-replace-all">
	    <xsl:param name="text"/>
	    <xsl:param name="replace"/>
	    <xsl:param name="by"/>
	    <xsl:choose>
		<xsl:when test="contains($text, $replace)">
		    <xsl:value-of select="substring-before($text,$replace)"/>
		    <xsl:copy-of select="$by"/>
		    <xsl:call-template name="string-replace-all">
		        <xsl:with-param name="text" select="substring-after($text,$replace)"/>
		        <xsl:with-param name="replace" select="$replace"/>
		        <xsl:with-param name="by">
		            <xsl:copy-of select="$by"/>
		        </xsl:with-param>
		    </xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		    <xsl:value-of select="$text"/>
		</xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
	<!--  Fin de cambios con las estadisticas  -->
</xsl:stylesheet>
