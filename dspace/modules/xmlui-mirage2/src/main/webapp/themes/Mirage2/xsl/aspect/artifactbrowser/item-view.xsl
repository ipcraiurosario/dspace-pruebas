<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->

<!--
    Rendering specific to the item display page.

    Author: art.lowel at atmire.com
    Author: lieven.droogmans at atmire.com
    Author: ben at atmire.com
    Author: Alexey Maslov

-->

<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1" xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
    xmlns:dim="http://www.dspace.org/xmlns/dspace/dim" xmlns:local="http://dspace.org/namespace/local/" xmlns:xlink="http://www.w3.org/TR/xlink/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:oreatom="http://www.openarchives.org/ore/atom/" xmlns="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:encoder="xalan://java.net.URLEncoder" xmlns:util="org.dspace.app.xmlui.utils.XSLUtils" xmlns:jstring="java.lang.String"
    xmlns:rights="http://cosimo.stanford.edu/sdr/metsrights/" xmlns:confman="org.dspace.core.ConfigurationManager"
    xmlns:flexUtils="es.arvo.app.mediafilter.FlexpaperUtils" exclude-result-prefixes="xalan encoder i18n dri mets dim xlink xsl util jstring rights confman flexUtils">

    <xsl:output indent="yes" />

    <xsl:template name="itemSummaryView-DIM">
        <!-- Generate the info about the item from the metadata section -->
        <xsl:apply-templates select="./mets:dmdSec/mets:mdWrap[@OTHERMDTYPE='DIM']/mets:xmlData/dim:dim" mode="itemSummaryView-DIM" />

        <xsl:copy-of select="$SFXLink" />

        <!-- Generate the Creative Commons license information from the file section (DSpace deposit license hidden by default)
        <xsl:if test="./mets:fileSec/mets:fileGrp[@USE='CC-LICENSE' or @USE='LICENSE']">
            <div class="license-info table">
                <p>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.license-text</i18n:text>
                </p>
                <ul class="list-unstyled">
                    <xsl:apply-templates select="./mets:fileSec/mets:fileGrp[@USE='CC-LICENSE' or @USE='LICENSE']" mode="simple"/>
                </ul>
            </div>
        </xsl:if>-->


    </xsl:template>

    <!-- An item rendered in the detailView pattern, the "full item record" view of a DSpace item in Manakin. -->
    <xsl:template name="itemDetailView-DIM">
        <!-- Output all of the metadata about the item from the metadata section -->
        <xsl:apply-templates select="mets:dmdSec/mets:mdWrap[@OTHERMDTYPE='DIM']/mets:xmlData/dim:dim" mode="itemDetailView-DIM" />

        <!-- Generate the bitstream information from the file section -->
        <xsl:choose>
            <xsl:when test="./mets:fileSec/mets:fileGrp[@USE='CONTENT' or @USE='ORIGINAL' or @USE='LICENSE']/mets:file">
                <h3>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-head</i18n:text>
                </h3>
                <div class="file-list">
                    <xsl:apply-templates select="./mets:fileSec/mets:fileGrp[@USE='CONTENT' or @USE='ORIGINAL' or @USE='LICENSE' or @USE='CC-LICENSE']">
                        <xsl:with-param name="context" select="." />
                        <xsl:with-param name="primaryBitstream" select="./mets:structMap[@TYPE='LOGICAL']/mets:div[@TYPE='DSpace Item']/mets:fptr/@FILEID" />
                    </xsl:apply-templates>
                </div>
            </xsl:when>
            <!-- Special case for handling ORE resource maps stored as DSpace bitstreams -->
            <xsl:when test="./mets:fileSec/mets:fileGrp[@USE='ORE']">
                <xsl:apply-templates select="./mets:fileSec/mets:fileGrp[@USE='ORE']" mode="itemDetailView-DIM" />
            </xsl:when>
            <xsl:otherwise>
                <h2>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-head</i18n:text>
                </h2>
                <table class="ds-table file-list">
                    <tr class="ds-table-header-row">
                        <th>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-file</i18n:text>
                        </th>
                        <th>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-size</i18n:text>
                        </th>
                        <th>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-format</i18n:text>
                        </th>
                        <th>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-view</i18n:text>
                        </th>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <p>
                                <i18n:text>xmlui.dri2xhtml.METS-1.0.item-no-files</i18n:text>
                            </p>
                        </td>
                    </tr>
                </table>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template name="flexpaper-view">
        <xsl:param name="bitstreamData" select="flexUtils:getBitstreamData(mets:FLocat[@LOCTYPE='URL']/@xlink:href)" />
        <xsl:param name="mimetype" />
        <xsl:param name="label" />
        <xsl:param name="title" />
        <xsl:choose>
            <xsl:when test="not(contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n'))">
                <xsl:choose>
                    <xsl:when test="contains($mimetype,'pdf')">
                        <xsl:choose>
                            <xsl:when test="not(string-length($bitstreamData)=0)">
                                <!--<xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:title"/><br/>-->
                                <div class="bloque-archivos">
                                    <a>
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
						<!--<xsl:value-of select="jstring:replaceAll(flexUtils:cleanName(string(mets:FLocat[@LOCTYPE='URL']/@xlink:href)),'/bitstream/handle/','/flexpaper/handle/')" />-->
                                        </xsl:attribute>
                                        <!--<i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-view</i18n:text>-->
                                        <img>
                                        <xsl:attribute name="src">
                                            <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                                            <xsl:value-of select="substring-after($mimetype,'/')" />
                                            <xsl:choose>
                                                <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                                                    <xsl:text>-lock</xsl:text>
                                                </xsl:when>
                                            </xsl:choose>
                                            <xsl:text>.png</xsl:text>
                                        </xsl:attribute>
                                        <xsl:attribute name="class">
                                            <xsl:text>bitstream_icon</xsl:text>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="$mimetype" />
                                            <xsl:text>-</xsl:text>
                                            <xsl:value-of select="$label" />
                                        </xsl:attribute>
                                        </img>
                                    </a>
                                    <a class="flexpaper-view-link">
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="jstring:replaceAll(flexUtils:cleanName(string(mets:FLocat[@LOCTYPE='URL']/@xlink:href)),'/bitstream/handle/','/flexpaper/handle/')" />
                                        </xsl:attribute>
                                        <xsl:text>Visualizar</xsl:text>
                                    </a>
                                </div>
                                <!--  /  <a class="glyphicon glyphicon-download-alt">
								<xsl:attribute name="href">
									<xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href"/>
								</xsl:attribute>
								<xsl:attribute name="title">
									<xsl:value-of select="$mimetype"/><xsl:text>-</xsl:text><xsl:value-of select="$label"/>	
								</xsl:attribute>
								<-<i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-download</i18n:text>->	
							</a>-->
                            </xsl:when>
                            <xsl:otherwise>
                                <!--<a target="_blank" class="flexpaper-view-link">
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                                    </xsl:attribute>
                                    <img>
                                    <xsl:attribute name="src">
                                        <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                                        <xsl:value-of select="substring-after($mimetype,'/')" />
                                        <xsl:choose>
                                            <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                                                <xsl:text>-lock</xsl:text>
                                            </xsl:when>
                                        </xsl:choose>
                                        <xsl:text>.png</xsl:text>
                                    </xsl:attribute>
                                    <xsl:attribute name="class">
                                        <xsl:text>bitstream_icon</xsl:text>
                                    </xsl:attribute>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="$mimetype" />
                                        <xsl:text>-</xsl:text>
                                        <xsl:value-of select="$label" />
                                    </xsl:attribute>
                                    </img>
                                </a>-->
				<div class="bloque-archivos">
				<a target="_blank" class="flexpaper-view-link">
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                                        <!--<xsl:value-of select="jstring:replaceAll(flexUtils:cleanName(string(mets:FLocat[@LOCTYPE='URL']/@xlink:href)),'/bitstream/handle/','/flexpaper/handle/')" />-->
                                    </xsl:attribute>
                                    <img>
                                    <xsl:attribute name="src">
                                        <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                                        <xsl:value-of select="substring-after($mimetype,'/')" />
                                        <xsl:choose>
                                            <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                                                <xsl:text>-lock</xsl:text>
                                            </xsl:when>
                                        </xsl:choose>
                                        <xsl:text>.png</xsl:text>
                                    </xsl:attribute>
                                    <xsl:attribute name="class">
                                        <xsl:text>bitstream_icon</xsl:text>
                                    </xsl:attribute>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="$mimetype" />
                                        <xsl:text>-</xsl:text>
                                        <xsl:value-of select="$label" />
                                    </xsl:attribute>
                                    </img>
                                </a>
                                    <a class="flexpaper-view-link">
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="jstring:replaceAll(flexUtils:cleanName(string(mets:FLocat[@LOCTYPE='URL']/@xlink:href)),'/bitstream/handle/','/flexpaper/handle/')" />
                                        </xsl:attribute>
                                        <xsl:text>Visualizar</xsl:text>
                                    </a>
                                </div>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <!--<a class="no-authorized bloque-archivos">
                            <xsl:attribute name="href">
                                <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                            </xsl:attribute>
                            <img>
                            <xsl:attribute name="src">
                                <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                                <xsl:value-of select="substring-after($mimetype,'/')" />
                                <xsl:choose>
                                    <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                                        <xsl:text>-lock</xsl:text>
                                    </xsl:when>
                                </xsl:choose>
                                <xsl:text>.png</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="class">
                                <xsl:text>bitstream_icon</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="$mimetype" />
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="$label" />
                            </xsl:attribute>
                            </img>
                        </a>-->
			<div class="bloque-archivos">
                        <a class="no-authorized">
                            <xsl:attribute name="href">
                                <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                            </xsl:attribute>
                            <img>
                            <xsl:attribute name="src">
                                <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                                <xsl:value-of select="substring-after($mimetype,'/')" />
                                <xsl:choose>
                                    <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                                        <xsl:text>-lock</xsl:text>
                                    </xsl:when>
                                </xsl:choose>
                                <xsl:text>.png</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="class">
                                <xsl:text>bitstream_icon</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="$mimetype" />
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="$label" />
                            </xsl:attribute>
                            </img>
                        </a>
                                    <a class="flexpaper-view-link">
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="jstring:replaceAll(flexUtils:cleanName(string(mets:FLocat[@LOCTYPE='URL']/@xlink:href)),'/bitstream/handle/','/flexpaper/handle/')" />
                                        </xsl:attribute>
                                        <xsl:text>Visualizar</xsl:text>
                                    </a>
                       </div>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <a class="no-authorized">
                    <xsl:attribute name="href">
                        <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                    </xsl:attribute>
                    <img>
                    <xsl:attribute name="src">
                        <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                        <xsl:value-of select="substring-after($mimetype,'/')" />
                        <xsl:choose>
                            <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                                <xsl:text>-lock</xsl:text>
                            </xsl:when>
                        </xsl:choose>
                        <xsl:text>.png</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="class">
                        <xsl:text>bitstream_icon</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="$mimetype" />
                        <xsl:text>-</xsl:text>
                        <xsl:value-of select="$label" />
                    </xsl:attribute>
                    </img>

                </a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="dim:dim" mode="itemSummaryView-DIM">
        <div class="item-summary-view-metadata">
            <xsl:call-template name="itemSummaryView-DIM-title" />
            <div class="row">
                <div class="col-sm-3">
                    <xsl:call-template name="itemSummaryView-DIM-exports" />
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <xsl:call-template name="itemSummaryView-DIM-thumbnail" />
                        </div>
                        <div class="col-xs-12 col-sm-12">
                            <xsl:call-template name="itemSummaryView-DIM-file-section" />
                        </div>
                    </div>
                    <xsl:call-template name="itemSummaryView-DIM-date" />
                    <xsl:call-template name="itemSummaryView-DIM-authors" />
                    <xsl:call-template name="itemSummaryView-DIM-director" />
                    <!-- <xsl:call-template name="plumx">
						<xsl:with-param name="identifier_uri" select="dim:field[@element='identifier' and @qualifier='uri']"/>
					</xsl:call-template> -->

                    <xsl:call-template name="social_icons" />
                </div>
                <div class="col-sm-9">
                    <!-- HBC:plugin de youtube -->
                    <xsl:if test="dim:field[@element='relation' and @qualifier='youtube']/node()">
                        <div class="youtube_iframe">
                            <xsl:element name="iframe">
                                <xsl:attribute name="id">youtube_iframe</xsl:attribute>
                                <xsl:attribute name="class">cf</xsl:attribute>
                                <xsl:attribute name="style">width:100%;height:360px;</xsl:attribute>
                                <xsl:attribute name="src">
                                    <xsl:value-of select="dim:field[@element='relation' and @qualifier='youtube']" />
                                </xsl:attribute>
                                <xsl:attribute name="frameborder">0</xsl:attribute>
                                <xsl:comment />
                            </xsl:element>
                            <script>
                            window.onload = function() {
                                var iframe = document.getElementById("youtube_iframe");
                                var imgs = iframe.getElementsByTagName("img");
                                
                                console.log(imgs )
                            };
                            </script>
                        </div>
                    </xsl:if>
                    <!-- HBC:plugin de video -->
                    <xsl:if test="not(dim:field[@element='relation' and @qualifier='youtube']/node())">
                        <xsl:for-each select="//mets:fileSec/mets:fileGrp[@USE='CONTENT' or @USE='ORIGINAL' or @USE='LICENSE']/mets:file">
                            <xsl:variable name="mime">
                                <xsl:value-of select="@MIMETYPE" />
                            </xsl:variable>
                            <xsl:variable name="archivo">
                                <xsl:value-of select="./mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                            </xsl:variable>
                            <xsl:if test="contains($mime,'video/mp4')">
                                <xsl:call-template name="showVideo">
                                    <xsl:with-param name="src">
                                        <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                                    </xsl:with-param>
                                    <xsl:with-param name="mimetype">
                                        <xsl:value-of select="$mime" />
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:if>
                            <xsl:if test="contains($mime,'audio')">
                                <xsl:call-template name="showVideo">
                                    <xsl:with-param name="src">
                                        <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                                    </xsl:with-param>
                                    <xsl:with-param name="mimetype">
                                        <xsl:value-of select="$mime" />
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:if>


                    <!-- EVALUACION REPEC-->
                    <!-- <xsl:if test="dim:field[@element='relation' and @qualifier='uri']/node()">-->
                    <xsl:if test="contains(dim:field[@element='relation' and @qualifier='uri'and descendant::text()],'repec')">
                        <xsl:call-template name="itemSummaryView-DIM-URI-REPEC" />
                    </xsl:if>
                    <xsl:if test="not(contains(dim:field[@element='relation' and @qualifier='uri'and descendant::text()],'repec'))">
                        <xsl:call-template name="itemSummaryView-DIM-URI" />
                    </xsl:if>
                    <!-- 	</xsl:if>-->
                    <!-- FIN EVALUACION REPEC-->
                    <hr />
                    <xsl:call-template name="itemSummaryView-DIM-description" />
                    <xsl:call-template name="itemSummaryView-DIM-descriptionabstract" />
                    <!--<xsl:call-template name="itemSummaryView-DIM-subject"/>-->
                    <xsl:call-template name="itemSummaryView-DIM-subjectlemb-ddc" />
                    <xsl:call-template name="itemSummaryView-DIM-local-pueblo" />
                    <xsl:call-template name="itemSummaryView-DIM-local-region" />
                    <xsl:call-template name="itemSummaryView-DIM-local-institution" />
                    <xsl:call-template name="itemSummaryView-DIM-URI-Fuente" />
                    
                   

                    <xsl:if test="$ds_item_view_toggle_url != ''">
                        <div class="text-center">
                            <hr />
                            <xsl:call-template name="itemSummaryView-show-full" />
                            <hr />
                        </div>
                    </xsl:if>
                    <xsl:call-template name="itemSummaryView-collections" />
                </div>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-title">
        <xsl:choose>
            <xsl:when test="count(dim:field[@element='title'][not(@qualifier)]) &gt; 1">
                <h2 class="page-header first-page-header">
                    <xsl:value-of select="dim:field[@element='title'][not(@qualifier)][1]/node()" />
                </h2>
                <!--
                <div class="simple-item-view-other">
                    <p class="lead">
                        <xsl:for-each select="dim:field[@element='title'][not(@qualifier)]">
                            <xsl:if test="not(position() = 1)">
                                <xsl:value-of select="./node()" />
                                <xsl:if test="count(following-sibling::dim:field[@element='title'][not(@qualifier)]) != 0">
                                    <xsl:text>; </xsl:text>
                                    <br />
                                </xsl:if>
                            </xsl:if>

                        </xsl:for-each>
                    </p>
                </div>
		-->
            </xsl:when>
            <xsl:when test="count(dim:field[@element='title'][not(@qualifier)]) = 1">
                <h2 class="page-header first-page-header">
                    <xsl:value-of select="dim:field[@element='title'][not(@qualifier)][1]/node()" />
                </h2>
            </xsl:when>
            <xsl:otherwise>
                <h2 class="page-header first-page-header">
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.no-title</i18n:text>
                </h2>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-thumbnail">
        
                        <xsl:variable name="mimetype">
                            <xsl:value-of select="dim:field[@element='format' and @qualifier='mimetype'][1]/node()"/>
                        </xsl:variable>
        <div class="thumbnail">
            <xsl:choose>
                <xsl:when test="//mets:fileSec/mets:fileGrp[@USE='THUMBNAIL']">
                    <xsl:variable name="src">
                        <xsl:choose>
                            <xsl:when test="/mets:METS/mets:fileSec/mets:fileGrp[@USE='THUMBNAIL']/mets:file[@GROUPID=../../mets:fileGrp[@USE='CONTENT']/mets:file[@GROUPID=../../mets:fileGrp[@USE='THUMBNAIL']/mets:file/@GROUPID][1]/@GROUPID]">
                                <xsl:value-of select="/mets:METS/mets:fileSec/mets:fileGrp[@USE='THUMBNAIL']/mets:file[@GROUPID=../../mets:fileGrp[@USE='CONTENT']/mets:file[@GROUPID=../../mets:fileGrp[@USE='THUMBNAIL']/mets:file/@GROUPID][1]/@GROUPID]/mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="//mets:fileSec/mets:fileGrp[@USE='THUMBNAIL']/mets:file/mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <!-- Checking if Thumbnail is restricted and if so, show a restricted image --> 
                    <xsl:choose>
                        <xsl:when test="contains($src,'isAllowed=n')"/>
                        <xsl:otherwise>
                            <img class="img-thumbnail" alt="Thumbnail">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$src"/>
                                </xsl:attribute>
                            </img>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="contains($mimetype,'video')">
                            <img alt="Thumbnail">
                            <xsl:attribute name="src">
                                <xsl:text>/themes/Mirage2/images/ur/portadaRI-video.png</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="class">
                                <xsl:text>img-thumbnail</xsl:text>
                            </xsl:attribute>
                            </img>
                        </xsl:when>
                        <xsl:when test="contains($mimetype,'audio')">
                            <img alt="Thumbnail">
                            <xsl:attribute name="src">
                                <xsl:text>/themes/Mirage2/images/ur/portadaRI-audio.png</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="class">
                                <xsl:text>img-thumbnail</xsl:text>
                            </xsl:attribute>
                            </img>
                        </xsl:when>
                        <xsl:otherwise>
                            <img alt="Thumbnail">
                            <xsl:attribute name="src">
                                <xsl:text>/themes/Mirage2/images/ur/portadaRI.png</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="class">
                                <xsl:text>img-thumbnail</xsl:text>
                            </xsl:attribute>
                            </img>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-description">
        <xsl:if test="dim:field[@element='description'][not(@qualifier)]">
            <div class="simple-item-view-description item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-resumen</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='description'][not(@qualifier)]">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <xsl:copy-of select="node()" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="count(following-sibling::dim:field[@element='description'][not(@qualifier)]) != 0">
                            <div class="spacer">&#160;</div>
                        </xsl:if>
                    </xsl:for-each>
                    <hr />
                </div>
            </div>
        </xsl:if>
    </xsl:template>


    <xsl:template name="itemSummaryView-DIM-descriptionabstract">
        <xsl:if test="dim:field[@element='description'and @qualifier='abstract']">
            <div class="simple-item-view-description item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-abstract</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='description'and @qualifier='abstract']">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <xsl:copy-of select="node()" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="count(following-sibling::dim:field[@element='description'and @qualifier='abstract']) != 0">
                            <div class="spacer">&#160;</div>
                        </xsl:if>
                    </xsl:for-each>
                    <hr />
                </div>
            </div>
        </xsl:if>
    </xsl:template>


    <xsl:template name="itemSummaryView-DIM-subject">
        <xsl:if test="dim:field[@element='subject'][not(@qualifier)]">
            <div class="simple-item-view-subject item-page-field-wrapper table jelspa">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-subject</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='subject'][not(@qualifier)]">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat($context-path,'/browse?type=subject&amp;value=')" />
                                        <xsl:copy-of select="." />
                                    </xsl:attribute>
                                    <xsl:value-of select="text()" />
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#44;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text> ; </xsl:text>
                    </xsl:for-each>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-subjectkeyword">
        <xsl:if test="dim:field[@element='subject' and @qualifier='keyword']">
            <div class="simple-item-view-subjectkeyword item-page-field-wrapper table jelspa">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-subjectkeyword</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='subject' and @qualifier='keyword']">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat($context-path,'/browse?type=subject&amp;value=')" />
                                        <xsl:copy-of select="." />
                                    </xsl:attribute>
                                    <xsl:value-of select="text()" />
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text> ; </xsl:text>
                    </xsl:for-each>
                </div>
            </div>
        </xsl:if>
    </xsl:template>



    <xsl:template name="itemSummaryView-DIM-subjectlemb-ddc">
        <xsl:if test="dim:field[@element='subject' and (@qualifier='lemb' or @qualifier='ddc')]">
            <div class="simple-item-view-subjectlemb item-page-field-wrapper table jelspa">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-subjectlemb</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='subject' and (@qualifier='lemb' or @qualifier='ddc')]">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat($context-path,'/discover?filtertype=subject&amp;filter_relational_operator=equals&amp;filter=')" />
                                        <xsl:value-of select="encoder:encode(text())" />
                                    </xsl:attribute>
                                    <xsl:value-of select="text()" />
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text> ; </xsl:text>
                    </xsl:for-each>
                </div>
            </div>
        </xsl:if>
    </xsl:template>


    <xsl:template name="itemSummaryView-DIM-local-pueblo">
        <xsl:if test="dim:field[@element='pueblo'][not(@qualifier)]">
            <div class="simple-item-view-subjectlemb item-page-field-wrapper table jelspa tesssssst">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-localpueblo</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='pueblo'][not(@qualifier)]">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <!--<xsl:value-of select="text()" />-->
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat($context-path,'/discover?filtertype=pueblo&amp;filter_relational_operator=equals&amp;filter=')" />
                                        <xsl:copy-of select="." />
                                    </xsl:attribute>
                                    <xsl:value-of select="text()" />
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text>. </xsl:text>
                    </xsl:for-each>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <xsl:template name="itemSummaryView-DIM-local-institution">
        <xsl:if test="dim:field[@element='institution'][not(@qualifier)]">
            <div class="simple-item-view-subjectlemb item-page-field-wrapper table jelspa tesssssst">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-localinstitution</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='institution'][not(@qualifier)]">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <!--<xsl:value-of select="text()" />-->
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat($context-path,'/discover?filtertype=institution&amp;filter_relational_operator=equals&amp;filter=')" />
                                        <xsl:copy-of select="." />
                                    </xsl:attribute>
                                    <xsl:value-of select="text()" />
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text>. </xsl:text>
                    </xsl:for-each>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <xsl:template name="itemSummaryView-DIM-local-region">
        <xsl:if test="dim:field[@element='region'][not(@qualifier)]">
            <div class="simple-item-view-subjectlemb item-page-field-wrapper table jelspa tesssssst">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-localregion</i18n:text>
                </h5>
                <div>
                    <xsl:for-each select="dim:field[@element='region'][not(@qualifier)]">
                        <xsl:choose>
                            <xsl:when test="node()">
                                <!--<xsl:value-of select="text()" />-->
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="concat($context-path,'/discover?filtertype=region&amp;filter_relational_operator=equals&amp;filter=')" />
                                        <xsl:copy-of select="." />
                                    </xsl:attribute>
                                    <xsl:value-of select="text()" />
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>&#160;</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text>. </xsl:text>
                    </xsl:for-each>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    


    <xsl:template name="itemSummaryView-DIM-authors">
        <xsl:if test="dim:field[@element='contributor'][@qualifier='author' and descendant::text()] or dim:field[@element='creator' and descendant::text()] or dim:field[@element='contributor' and descendant::text()]">
            <div class="simple-item-view-authors item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-author</i18n:text>
                </h5>
                <xsl:choose>
                    <xsl:when test="dim:field[@element='contributor'][@qualifier='author']">
                        <xsl:for-each select="dim:field[@element='contributor'][@qualifier='author']">
                            <xsl:call-template name="itemSummaryView-DIM-authors-entry" />
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:when test="dim:field[@element='creator']">
                        <xsl:for-each select="dim:field[@element='creator'][not(@qualifier)]">
                            <xsl:call-template name="itemSummaryView-DIM-authors-entry" />
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:when test="dim:field[@element='contributor']">
                        <xsl:for-each select="dim:field[@element='contributor']">
                            <xsl:call-template name="itemSummaryView-DIM-authors-entry" />
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.no-author</i18n:text>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-director">
        <xsl:if test="dim:field[@element='contributor'][@qualifier='advisor' and descendant::text()] ">
            <div class="simple-item-view-authors item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-director</i18n:text>
                </h5>
                <xsl:choose>
                    <xsl:when test="dim:field[@element='contributor'][@qualifier='advisor']">
                        <xsl:for-each select="dim:field[@element='contributor'][@qualifier='advisor']">
                            <xsl:call-template name="itemSummaryView-DIM-authors-entry" />
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.no-author</i18n:text>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-authors-entry">
        <span>
            <xsl:if test="@authority and string-length(@authority) &lt; 30 and @confidence and (@confidence='ACCEPTED' or @confidence='UNCERTAIN')">
                <xsl:attribute name="class">
                    <xsl:text>ds-dc_contributor_author-authority</xsl:text>
                </xsl:attribute>
            </xsl:if>
            <a>
                <xsl:attribute name="href">
                    <xsl:choose>
                        <xsl:when test="@authority">
                            <xsl:value-of select="concat($context-path,'/browse?authority=')" />
                            <xsl:value-of select="@authority" />
                            <xsl:text>&amp;type=author</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat($context-path,'/browse?value=')" />
                            <xsl:copy-of select="." />
                            <xsl:text>&amp;type=author</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>

                </xsl:attribute>
                <xsl:copy-of select="node()" />
                <xsl:if test="@authority and string-length(@authority) &lt; 30">
                    <img class="logo-autoridad">
                    <xsl:attribute name="title">
                        <xsl:text>xmlui.authority.rosario-auth</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                        <xsl:text>xmlui.authority.rosario-auth</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="i18n:attr">
                        <xsl:text>alt title</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="src">
                        <xsl:value-of select="$context-path" />
                        <xsl:text>/themes/Mirage2/images/autoridades/rosario.ico</xsl:text>
                    </xsl:attribute>
                    </img>
                </xsl:if>
            </a>
        </span>
        <br></br>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-URI">
        <!--<xsl:if test="dim:field[@element='identifier' and @qualifier='uri' and descendant::text()]">-->
        <xsl:if test="dim:field[@element='identifier' and @qualifier='uri']">
            <div class="simple-item-view-uri item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-uri</i18n:text>
                </h5>
                <span>
                    <xsl:for-each select="dim:field[@element='identifier' and @qualifier='uri']">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:copy-of select="./node()" />
                            </xsl:attribute>
                            <xsl:copy-of select="./node()" />
                        </a>
                        <xsl:if test="count(following-sibling::dim:field[@element='identifier' and @qualifier='uri']) != 0">
                            <br />
                        </xsl:if>
                    </xsl:for-each>

                    <xsl:if test="dim:field[@element='identifier'][not(@qualifier)]">
                        <br />
                        <xsl:for-each select="dim:field[@element='identifier'][not(@qualifier)]">
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:if test="contains(node(),'10.12804/revistas')">
                                        <xsl:text>http://dx.doi.org/</xsl:text>
                                    </xsl:if>
                                    <xsl:copy-of select="./node()" />
                                </xsl:attribute>
                                <xsl:copy-of select="./node()" />
                            </a>
                            <xsl:if test="count(following-sibling::dim:field[@element='identifier'][not(@qualifier)]) != 0">
                                <br />
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:if>

                    <xsl:if test="dim:field[@element='description' and @qualifier='doi']">
                        <br />
                        <xsl:for-each select="dim:field[@element='description' and @qualifier='doi']">
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:copy-of select="./node()" />
                                </xsl:attribute>
                                <xsl:copy-of select="./node()" />
                            </a>
                            <xsl:if test="count(following-sibling::dim:field[@element='description'and @qualifier='doi']) != 0">
                                <br />
                            </xsl:if>
                        </xsl:for-each>

                    </xsl:if>
                </span>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-URI-REPEC">
        <xsl:if test="dim:field[@element='identifier' and @qualifier='uri' and descendant::text()]">
            <div class="simple-item-view-uri item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-uri</i18n:text>
                </h5>
                <span>
                    <xsl:for-each select="dim:field[@element='relation' and @qualifier='uri']">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:copy-of select="./node()" />
                            </xsl:attribute>
                            <xsl:copy-of select="./node()" />
                        </a>
                        <xsl:if test="count(following-sibling::dim:field[@element='relation' and @qualifier='uri']) != 0">
                            <br />
                        </xsl:if>
                    </xsl:for-each>
                </span>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-URI-Fuente">
        <xsl:if test="dim:field[@element='relation' and @qualifier='uri' and descendant::text()]">
            <div class="simple-item-view-uri item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-relationuri</i18n:text>
                </h5>
                <span>
                    <xsl:for-each select="dim:field[@element='relation' and @qualifier='uri']">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:copy-of select="./node()" />
                            </xsl:attribute>
                            <xsl:attribute name="target">
                                <xsl:text>_blank</xsl:text>
                            </xsl:attribute>
                            <xsl:copy-of select="./node()" />
                        </a>
                        <xsl:if test="count(following-sibling::dim:field[@element='relation' and @qualifier='uri']) != 0">
                            <br />
                        </xsl:if>
                    </xsl:for-each>
                </span>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-date">
        <xsl:if test="dim:field[@element='date' and @qualifier='issued' and descendant::text()]">
            <div class="simple-item-view-date word-break item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.item-date</i18n:text>
                </h5>
                <xsl:for-each select="dim:field[@element='date' and @qualifier='issued']">
                    <xsl:copy-of select="substring(./node(),1,10)" />
                    <xsl:if test="count(following-sibling::dim:field[@element='date' and @qualifier='issued']) != 0">
                        <br />
                    </xsl:if>
                </xsl:for-each>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-show-full">
        <div class="simple-item-view-show-full item-page-field-wrapper table">
            <!--<h5>
                <i18n:text>xmlui.mirage2.itemSummaryView.MetaData</i18n:text>
            </h5>-->
            <a>
                <xsl:attribute name="href">
                    <xsl:value-of select="$ds_item_view_toggle_url" />
                </xsl:attribute>
                <i18n:text>xmlui.ArtifactBrowser.ItemViewer.show_full</i18n:text>
            </a>
        </div>
    </xsl:template>

    <xsl:template name="itemSummaryView-collections">
        <xsl:if test="$document//dri:referenceSet[@id='aspect.artifactbrowser.ItemViewer.referenceSet.collection-viewer']">
            <div class="simple-item-view-collections item-page-field-wrapper table">
                <h5>
                    <i18n:text>xmlui.mirage2.itemSummaryView.Collections</i18n:text>
                </h5>
                <xsl:apply-templates select="$document//dri:referenceSet[@id='aspect.artifactbrowser.ItemViewer.referenceSet.collection-viewer']/dri:reference" />
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-file-section">
        <xsl:choose>
            <xsl:when test="//mets:fileSec/mets:fileGrp[@USE='CONTENT' or @USE='ORIGINAL' or @USE='LICENSE']/mets:file">
                <div class="item-page-field-wrapper table word-break">
                    <!--<h5>
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-viewOpen</i18n:text>
                    </h5>-->

                    <xsl:variable name="label-1">
                        <xsl:choose>
                            <xsl:when test="confman:getProperty('mirage2.item-view.bitstream.href.label.1')">
                                <xsl:value-of select="confman:getProperty('mirage2.item-view.bitstream.href.label.1')" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>label</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:variable name="label-2">
                        <xsl:choose>
                            <xsl:when test="confman:getProperty('mirage2.item-view.bitstream.href.label.2')">
                                <xsl:value-of select="confman:getProperty('mirage2.item-view.bitstream.href.label.2')" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>title</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <!-- HBC:No contiene Repec en identifier.uri-->
                    <xsl:if test="not(contains(dim:field[@element='relation' and @qualifier='uri'and descendant::text()],'repec') )">
                        <!--<button data-toggle="collapse" data-target="#filelist">
							<xsl:attribute name="class">
								<xsl:text>btn</xsl:text>
							</xsl:attribute>
							<i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-viewOpen</i18n:text>
						</button>-->
                        <div id="filelist" class="all_filelist">
                            <xsl:for-each select="//mets:fileSec/mets:fileGrp[@USE='CONTENT' or @USE='ORIGINAL' or @USE='LICENSE']/mets:file">
                                <xsl:call-template name="flexpaper-view">
                                    <xsl:with-param name="mimetype" select="@MIMETYPE" />
                                    <xsl:with-param name="label" select="mets:FLocat[@LOCTYPE='URL']/@xlink:label" />
                                    <xsl:with-param name="title" select="mets:FLocat[@LOCTYPE='URL']/@xlink:title" />
                                </xsl:call-template>
                            </xsl:for-each>
                        </div>
                    </xsl:if>
                    <xsl:if test="contains(dim:field[@element='relation' and @qualifier='uri'and descendant::text()],'repec')">
                        <xsl:for-each select="//mets:fileSec/mets:fileGrp[@USE='CONTENT' or @USE='ORIGINAL' or @USE='LICENSE']/mets:file">
                            <xsl:if test="contains(substring-after(@MIMETYPE,'/'),'html')">
                                <!--<span><xsl:value-of select="substring-after(@MIMETYPE,'/')"/></span>-->
                                <!--<xsl:call-template name="itemSummaryView-DIM-file-section-entry">
								<xsl:with-param name="href" select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
								<xsl:with-param name="mimetype" select="@MIMETYPE" />
								<xsl:with-param name="label-1" select="$label-1" />
								<xsl:with-param name="label-2" select="$label-2" />
								<xsl:with-param name="title" select="mets:FLocat[@LOCTYPE='URL']/@xlink:title" />
								<xsl:with-param name="label" select="mets:FLocat[@LOCTYPE='URL']/@xlink:label" />
								<xsl:with-param name="size" select="@SIZE" />
							</xsl:call-template>-->
                                <xsl:call-template name="flexpaper-view">
                                    <xsl:with-param name="mimetype" select="@MIMETYPE" />
                                    <xsl:with-param name="label" select="mets:FLocat[@LOCTYPE='URL']/@xlink:label" />
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:if>

                </div>
            </xsl:when>
            <!-- Special case for handling ORE resource maps stored as DSpace bitstreams -->
            <xsl:when test="//mets:fileSec/mets:fileGrp[@USE='ORE']">
                <xsl:apply-templates select="//mets:fileSec/mets:fileGrp[@USE='ORE']" mode="itemSummaryView-DIM" />
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-file-section-entry">
        <xsl:param name="href" />
        <xsl:param name="mimetype" />
        <xsl:param name="label-1" />
        <xsl:param name="label-2" />
        <xsl:param name="title" />
        <xsl:param name="label" />
        <xsl:param name="size" />
        <div>

            <a>
                <xsl:attribute name="title">
                    <xsl:choose>
                        <xsl:when test="contains($label-1, 'label') and string-length($label)!=0">
                            <xsl:value-of select="$label" />
                        </xsl:when>
                        <xsl:when test="contains($label-1, 'title') and string-length($title)!=0">
                            <xsl:value-of select="$title" />
                        </xsl:when>
                        <xsl:when test="contains($label-2, 'label') and string-length($label)!=0">
                            <xsl:value-of select="$label" />
                        </xsl:when>
                        <xsl:when test="contains($label-2, 'title') and string-length($title)!=0">
                            <xsl:value-of select="$title" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="getFileTypeDesc">
                                <xsl:with-param name="mimetype">
                                    <xsl:value-of select="substring-before($mimetype,'/')" />
                                    <xsl:text>/</xsl:text>
                                    <xsl:choose>
                                        <xsl:when test="contains($mimetype,';')">
                                            <xsl:value-of select="substring-before(substring-after($mimetype,'/'),';')" />
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="substring-after($mimetype,'/')" />
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text> (</xsl:text>
                    <xsl:choose>
                        <xsl:when test="$size &lt; 1024">
                            <xsl:value-of select="$size" />
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.size-bytes</i18n:text>
                        </xsl:when>
                        <xsl:when test="$size &lt; 1024 * 1024">
                            <xsl:value-of select="substring(string($size div 1024),1,5)" />
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.size-kilobytes</i18n:text>
                        </xsl:when>
                        <xsl:when test="$size &lt; 1024 * 1024 * 1024">
                            <xsl:value-of select="substring(string($size div (1024 * 1024)),1,5)" />
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.size-megabytes</i18n:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="substring(string($size div (1024 * 1024 * 1024)),1,5)" />
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.size-gigabytes</i18n:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text> )</xsl:text>
                </xsl:attribute>
                <img>
                <xsl:attribute name="src">
                    <xsl:text>/themes/Mirage2/images/ur/</xsl:text>
                    <xsl:value-of select="substring-after($mimetype,'/')" />
                    <xsl:choose>
                        <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                            <xsl:text>-lock</xsl:text>
                        </xsl:when>
                    </xsl:choose>

                    <xsl:text>.png</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="class">
                    <xsl:text>bitstream_icon</xsl:text>
                </xsl:attribute>
                </img>
            </a>
            <xsl:call-template name="itemSummaryView-DIM-preview-section">
                <xsl:with-param name="mimetype">
                    <xsl:value-of select="substring-before($mimetype,'/')" />
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="substring-after($mimetype,'/')" />
                </xsl:with-param>
            </xsl:call-template>
        </div>
    </xsl:template>

    <xsl:template name="itemSummaryView-DIM-preview-section">
        <xsl:param name="mimetype" />
        <xsl:if test="$mimetype = 'video/mp4' or $mimetype = 'audio/mp3' or contains($mimetype,'flv')">
            <div id="file-preview" class="row">
                <div class="col-xs-12">
                    <xsl:call-template name="showVideo">
                        <xsl:with-param name="src">
                            <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                        </xsl:with-param>
                        <xsl:with-param name="mimetype">
                            <xsl:value-of select="$mimetype" />
                        </xsl:with-param>
                    </xsl:call-template>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="dim:dim" mode="itemDetailView-DIM">
        <xsl:call-template name="itemSummaryView-DIM-title" />
        <div class="ds-table-responsive">
            <table class="ds-includeSet-table detailtable table table-striped table-hover">
                <xsl:apply-templates mode="itemDetailView-DIM" />
            </table>
        </div>

        <span class="Z3988">
            <xsl:attribute name="title">
                <xsl:call-template name="renderCOinS" />
            </xsl:attribute>
            &#xFEFF;
            <!-- non-breaking space to force separating the end tag -->
        </span>
        <xsl:copy-of select="$SFXLink" />
    </xsl:template>

    <xsl:template match="dim:field" mode="itemDetailView-DIM">
        <tr>
            <xsl:attribute name="class">
                <xsl:text>ds-table-row </xsl:text>
                <xsl:if test="(position() div 2 mod 2 = 0)">even </xsl:if>
                <xsl:if test="(position() div 2 mod 2 = 1)">odd </xsl:if>
            </xsl:attribute>
            <td class="label-cell">
                <xsl:value-of select="./@mdschema" />
                <xsl:text>.</xsl:text>
                <xsl:value-of select="./@element" />
                <xsl:if test="./@qualifier">
                    <xsl:text>.</xsl:text>
                    <xsl:value-of select="./@qualifier" />
                </xsl:if>
            </td>
            <td class="word-break">
                <xsl:copy-of select="./node()" />
                <xsl:if test="./@authority and ./@confidence">
                    <xsl:call-template name="authorityConfidenceIcon">
                        <xsl:with-param name="confidence" select="./@confidence" />
                    </xsl:call-template>
                </xsl:if>
            </td>
            <!--<td><xsl:value-of select="./@language"/></td>-->
        </tr>
    </xsl:template>

    <!-- don't render the item-view-toggle automatically in the summary view, only when it gets called -->
    <xsl:template match="dri:p[contains(@rend , 'item-view-toggle') and
        (preceding-sibling::dri:referenceSet[@type = 'summaryView'] or following-sibling::dri:referenceSet[@type = 'summaryView'])]">
    </xsl:template>

    <!-- don't render the head on the item view page -->
    <xsl:template match="dri:div[@n='item-view']/dri:head" priority="5">
    </xsl:template>

    <xsl:template match="mets:fileGrp[@USE='CONTENT']">
        <xsl:param name="context" />
        <xsl:param name="primaryBitstream" select="-1" />
        <xsl:choose>
            <!-- If one exists and it's of text/html MIME type, only display the primary bitstream -->
            <xsl:when test="mets:file[@ID=$primaryBitstream]/@MIMETYPE='text/html'">
                <xsl:apply-templates select="mets:file[@ID=$primaryBitstream]">
                    <xsl:with-param name="context" select="$context" />
                </xsl:apply-templates>
            </xsl:when>
            <!-- Otherwise, iterate over and display all of them -->
            <xsl:otherwise>
                <xsl:apply-templates select="mets:file">
                    <!--Do not sort any more bitstream order can be changed-->
                    <xsl:with-param name="context" select="$context" />
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="mets:fileGrp[@USE='LICENSE']">
        <xsl:param name="context" />
        <xsl:param name="primaryBitstream" select="-1" />
        <xsl:apply-templates select="mets:file">
            <xsl:with-param name="context" select="$context" />
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="mets:file">
        <xsl:param name="context" select="." />
        <div class="file-wrapper row">
            <div class="col-xs-6 col-sm-3">
                <div class="thumbnail">
                    <a class="image-link">
                        <xsl:attribute name="href">
                            <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                        </xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="$context/mets:fileSec/mets:fileGrp[@USE='THUMBNAIL']/mets:file[@GROUPID=current()/@GROUPID]">
                                <img alt="Thumbnail">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="$context/mets:fileSec/mets:fileGrp[@USE='THUMBNAIL']/mets:file[@GROUPID=current()/@GROUPID]/mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
                                </xsl:attribute>
                                </img>
                            </xsl:when>
                            <xsl:otherwise>
                                <!--<img alt="Thumbnail">
                                    <xsl:attribute name="data-src">
                                        <xsl:text>holder.js/100%x</xsl:text>
                                        <xsl:value-of select="$thumbnail.maxheight"/>
                                        <xsl:text>/text:No Thumbnail</xsl:text>
                                    </xsl:attribute>
									
                                </img>-->
                                <i18n:text>xmlui.detail-item-view.nothumbnail</i18n:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </a>
                </div>
            </div>

            <div class="col-xs-6 col-sm-7">
                <dl class="file-metadata dl-horizontal">
                    <dt>
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-name</i18n:text>
                        <xsl:text>:</xsl:text>
                    </dt>
                    <dd class="word-break">
                        <xsl:attribute name="title">
                            <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:title" />
                        </xsl:attribute>
                        <xsl:value-of select="util:shortenString(mets:FLocat[@LOCTYPE='URL']/@xlink:title, 30, 5)" />
                    </dd>
                    <!-- File size always comes in bytes and thus needs conversion -->
                    <dt>
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-size</i18n:text>
                        <xsl:text>:</xsl:text>
                    </dt>
                    <dd class="word-break">
                        <xsl:choose>
                            <xsl:when test="@SIZE &lt; 1024">
                                <xsl:value-of select="@SIZE" />
                                <i18n:text>xmlui.dri2xhtml.METS-1.0.size-bytes</i18n:text>
                            </xsl:when>
                            <xsl:when test="@SIZE &lt; 1024 * 1024">
                                <xsl:value-of select="substring(string(@SIZE div 1024),1,5)" />
                                <i18n:text>xmlui.dri2xhtml.METS-1.0.size-kilobytes</i18n:text>
                            </xsl:when>
                            <xsl:when test="@SIZE &lt; 1024 * 1024 * 1024">
                                <xsl:value-of select="substring(string(@SIZE div (1024 * 1024)),1,5)" />
                                <i18n:text>xmlui.dri2xhtml.METS-1.0.size-megabytes</i18n:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="substring(string(@SIZE div (1024 * 1024 * 1024)),1,5)" />
                                <i18n:text>xmlui.dri2xhtml.METS-1.0.size-gigabytes</i18n:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </dd>
                    <!-- Lookup File Type description in local messages.xml based on MIME Type.
         In the original DSpace, this would get resolved to an application via
         the Bitstream Registry, but we are constrained by the capabilities of METS
         and can't really pass that info through. -->
                    <dt>
                        <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-format</i18n:text>
                        <xsl:text>:</xsl:text>
                    </dt>
                    <dd class="word-break">
                        <xsl:call-template name="getFileTypeDesc">
                            <xsl:with-param name="mimetype">
                                <xsl:value-of select="substring-before(@MIMETYPE,'/')" />
                                <xsl:text>/</xsl:text>
                                <xsl:choose>
                                    <xsl:when test="contains(@MIMETYPE,';')">
                                        <xsl:value-of select="substring-before(substring-after(@MIMETYPE,'/'),';')" />
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="substring-after(@MIMETYPE,'/')" />
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                        </xsl:call-template>
                    </dd>
                    <!-- Display the contents of 'Description' only if bitstream contains a description -->
                    <xsl:if test="mets:FLocat[@LOCTYPE='URL']/@xlink:label != ''">
                        <dt>
                            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-description</i18n:text>
                            <xsl:text>:</xsl:text>
                        </dt>
                        <dd class="word-break">
                            <xsl:attribute name="title">
                                <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:label" />
                            </xsl:attribute>
                            <xsl:value-of select="util:shortenString(mets:FLocat[@LOCTYPE='URL']/@xlink:label, 30, 5)" />
                        </dd>
                    </xsl:if>
                </dl>
            </div>
        </div>

    </xsl:template>

    <xsl:template name="view-open">
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="mets:FLocat[@LOCTYPE='URL']/@xlink:href" />
            </xsl:attribute>
            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-files-viewOpen</i18n:text>
        </a>
    </xsl:template>

    <xsl:template name="display-rights">
        <xsl:variable name="file_id" select="jstring:replaceAll(jstring:replaceAll(string(@ADMID), '_METSRIGHTS', ''), 'rightsMD_', '')" />
        <xsl:variable name="rights_declaration" select="../../../mets:amdSec/mets:rightsMD[@ID = concat('rightsMD_', $file_id, '_METSRIGHTS')]/mets:mdWrap/mets:xmlData/rights:RightsDeclarationMD" />
        <xsl:variable name="rights_context" select="$rights_declaration/rights:Context" />
        <xsl:variable name="users">
            <xsl:for-each select="$rights_declaration/*">
                <xsl:value-of select="rights:UserName" />
                <xsl:choose>
                    <xsl:when test="rights:UserName/@USERTYPE = 'GROUP'">
                        <xsl:text> (group)</xsl:text>
                    </xsl:when>
                    <xsl:when test="rights:UserName/@USERTYPE = 'INDIVIDUAL'">
                        <xsl:text> (individual)</xsl:text>
                    </xsl:when>
                </xsl:choose>
                <xsl:if test="position() != last()">, </xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="not ($rights_context/@CONTEXTCLASS = 'GENERAL PUBLIC') and ($rights_context/rights:Permissions/@DISPLAY = 'true')">
                <a href="{mets:FLocat[@LOCTYPE='URL']/@xlink:href}">
                    <img width="64" height="64" src="{concat($theme-path,'/images/Crystal_Clear_action_lock3_64px.png')}"
                        title="Read access available for {$users}" />
                    <!-- icon source: http://commons.wikimedia.org/wiki/File:Crystal_Clear_action_lock3.png -->
                </a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="view-open" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="getFileIcon">
        <xsl:param name="mimetype" />
        <i aria-hidden="true">
            <xsl:attribute name="class">
                <xsl:text>glyphicon </xsl:text>
                <xsl:choose>
                    <xsl:when test="contains(mets:FLocat[@LOCTYPE='URL']/@xlink:href,'isAllowed=n')">
                        <xsl:text> glyphicon-lock</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> glyphicon-file</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </i>
        <xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template name="social_icons">
        <h5 id="visible-xs">
            <i18n:text>xmlui.dri2xhtml.METS-1.0.item-social-icons</i18n:text>
        </h5>
        <!-- Go to www.addthis.com/dashboard to customize your tools -->
        <div class="addthis_inline_share_toolbox">
            <script type="text/javascript">
				<xsl:attribute name="src">
					<xsl:text>http://s7.addthis.com//js/300/addthis_widget.js#pubid=ra-5810a64cf1ec5f30</xsl:text>
				</xsl:attribute>
			</script>
		</div>
    </xsl:template>

    <!-- <xsl:template name="plumx">
	<xsl:param name="identifier_uri"/>
	<div class="plumx-altmetrics">
		<h5 id="visible-xs"><i18n:text>xmlui.dri2xhtml.METS-1.0.altmetrics-title</i18n:text></h5>
		<a>
			<xsl:attribute name="href">https://plu.mx/plum/a/?repo_url=<xsl:value-of select="$identifier_uri"/></xsl:attribute>
			<xsl:attribute name="class">plumx-plum-print-popup</xsl:attribute>
			<xsl:attribute name="data-popup">top</xsl:attribute>
			<xsl:attribute name="data-hide-when-empty">true</xsl:attribute>
			<xsl:attribute name="data-site">plum</xsl:attribute>
			<xsl:attribute name="data-badge">false</xsl:attribute>
			
		</a>
	</div>
	</xsl:template>-->

    <!-- Generate the license information from the file section -->
    <xsl:template match="mets:fileGrp[@USE='CC-LICENSE']" mode="simple">
        <li><a href="{mets:file/mets:FLocat[@xlink:title='license_text']/@xlink:href}">
                <i18n:text>xmlui.dri2xhtml.structural.link_cc</i18n:text>
            </a></li>
    </xsl:template>

    <!-- Generate the license information from the file section -->
    <xsl:template match="mets:fileGrp[@USE='LICENSE']" mode="simple">
        <li><a href="{mets:file/mets:FLocat[@xlink:title='license.txt']/@xlink:href}">
                <i18n:text>xmlui.dri2xhtml.structural.link_original_license</i18n:text>
            </a></li>
    </xsl:template>

    <!--
    File Type Mapping template

    This maps format MIME Types to human friendly File Type descriptions.
    Essentially, it looks for a corresponding 'key' in your messages.xml of this
    format: xmlui.dri2xhtml.mimetype.{MIME Type}

    (e.g.) <message key="xmlui.dri2xhtml.mimetype.application/pdf">PDF</message>

    If a key is found, the translated value is displayed as the File Type (e.g. PDF)
    If a key is NOT found, the MIME Type is displayed by default (e.g. application/pdf)
    -->
    <xsl:template name="getFileTypeDesc">
        <xsl:param name="mimetype" />

        <!--Build full key name for MIME type (format: xmlui.dri2xhtml.mimetype.{MIME type})-->
        <xsl:variable name="mimetype-key">xmlui.dri2xhtml.mimetype.
            <xsl:value-of select='$mimetype' />
        </xsl:variable>

        <!--Lookup the MIME Type's key in messages.xml language file.  If not found, just display MIME Type-->
        <i18n:text i18n:key="{$mimetype-key}">
            <xsl:value-of select="$mimetype" />
        </i18n:text>
    </xsl:template>


</xsl:stylesheet>
