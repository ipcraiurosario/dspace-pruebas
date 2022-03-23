<!-- The contents of this file are subject to the license and copyright detailed 
	in the LICENSE and NOTICE files at the root of the source tree and available 
	online at http://www.dspace.org/license/ -->
<!-- Rendering specific to the item display page. Author: Adan Roman -->

<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:dim="http://www.dspace.org/xmlns/dspace/dim" xmlns:xlink="http://www.w3.org/TR/xlink/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:atom="http://www.w3.org/2005/Atom" xmlns:ore="http://www.openarchives.org/ore/terms/"
	xmlns:oreatom="http://www.openarchives.org/ore/atom/" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xalan="http://xml.apache.org/xalan" xmlns:encoder="xalan://java.net.URLEncoder"
	xmlns:util="org.dspace.app.xmlui.utils.XSLUtils" xmlns:jstring="java.lang.String"
	xmlns:rights="http://cosimo.stanford.edu/sdr/metsrights/"
	xmlns:confman="org.dspace.core.ConfigurationManager" xmlns:wos="es.arvo.wos.WokUtils"
	exclude-result-prefixes="xalan encoder i18n dri mets dim xlink xsl util jstring rights wos">

	<xsl:output indent="yes" />

	<xsl:template name="share_links">
		<!-- Go to www.addthis.com/dashboard to customize your tools -->
		<script type="text/javascript"
			src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-589afda1c66476e5"></script>
	</xsl:template>
	
	<xsl:template name="itemSummaryView-DIM-exports">
		<xsl:param name="link" select="//@OBJID" />
		<xsl:variable name="dspace.url" select="confman:getProperty('dspace.url')" />
		<xsl:variable name="refworksVendor"
			select="confman:getProperty('refworks.vendor')" />
		<xsl:if test="$link">

			<ul class="nav navbar-nav">
				<li class="dropdown">
					<a aria-expanded="false" role="button" data-toggle="dropdown"
						class="dropdown-toggle" href="#">
						<i18n:text>xmlui.item-view-buttons.exportar</i18n:text>
						<!-- <span class="caret"></span>-->
					</a>
					<ul role="menu" class="dropdown-menu">
						<!--<li>
							<a>
								<xsl:attribute name="href">
									<xsl:value-of
										select="concat(substring-before($link,'handle'),'ris/handle',substring-after($link,'handle'))" />
								</xsl:attribute>
								<i18n:text>xmlui.BibliographyReader.ris</i18n:text>
							</a>
						</li>-->
						<li>
							<a target="_blank">
								<xsl:attribute name="href">
												<xsl:text>https://www.mendeley.com/import/?url=</xsl:text>
												<xsl:value-of
									select="//dim:field[@element='identifier' and @qualifier='uri']" />
											</xsl:attribute>
								<xsl:attribute name="class">
												<xsl:text>mendeley_button</xsl:text>
											</xsl:attribute>
								<i18n:text>xmlui.BibliographyReader.mendeley</i18n:text>
							</a>
						</li>
						<li>
							<a id="bibtex">
								<xsl:attribute name="href">
									<xsl:value-of
										select="concat(substring-before($link,'handle'),'bibtex/handle',substring-after($link,'handle'))" />
								</xsl:attribute>
								<i18n:text>xmlui.BibliographyReader.bibtex</i18n:text>
							</a>
						</li>
						<!-- <li>
							<a id="apa">
								<xsl:attribute name="href">
									<xsl:value-of
										select="concat(substring-before($link,'handle'),'cita/handle',substring-after($link,'handle'))" />
								</xsl:attribute>
								<i18n:text>xmlui.BibliographyReader.apa</i18n:text>
							</a>
						</li>-->
					</ul>
				</li>
			</ul>

		</xsl:if>

	</xsl:template>

</xsl:stylesheet>