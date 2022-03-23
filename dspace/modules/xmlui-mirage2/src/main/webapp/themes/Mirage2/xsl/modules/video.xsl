<!-- The contents of this file are subject to the license and copyright detailed 
	in the LICENSE and NOTICE files at the root of the source tree and available 
	online at http://www.dspace.org/license/ -->

<!-- Rendering video ando audio html5 viewer. 
		Author: AdÃ¡n RomÃ¡n Ruiz at	arvo.es -->
		
<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:dim="http://www.dspace.org/xmlns/dspace/dim" xmlns:xlink="http://www.w3.org/TR/xlink/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:atom="http://www.w3.org/2005/Atom" xmlns:ore="http://www.openarchives.org/ore/terms/"
	xmlns:oreatom="http://www.openarchives.org/ore/atom/" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xalan="http://xml.apache.org/xalan" xmlns:encoder="xalan://java.net.URLEncoder"
	xmlns:util="org.dspace.app.xmlui.utils.XSLUtils" xmlns:jstring="java.lang.String"
	xmlns:rights="http://cosimo.stanford.edu/sdr/metsrights/"
	xmlns:confman="org.dspace.core.ConfigurationManager"
	exclude-result-prefixes="xalan encoder i18n dri mets dim xlink xsl util jstring rights confman">

	<xsl:output indent="yes" />
	<xsl:template name="showVideo">
		<xsl:param name="src" />
		<xsl:param name="mimetype" />
			<xsl:choose>
				<xsl:when test="$mimetype='video/mp4'">
					<video id="video-preview" width="100%" height="100%" controls="true">
						<source type="video/mp4">
							<xsl:attribute name="src">
		    					<xsl:value-of select="$src" />
		    				</xsl:attribute>
						</source>
						<xsl:text>Vídeo no compatible</xsl:text>
					</video>
				</xsl:when>
				<xsl:when test="$mimetype='video/x-flv'">
					<video id="video-preview" width="100%" height="100%" controls="true">
						<source type="video/x-flv">
							<xsl:attribute name="src">
		    					<xsl:value-of select="$src" />
		    				</xsl:attribute>
						</source>
						<xsl:text>Vídeo no compatible</xsl:text>
					</video>
				</xsl:when>
				<xsl:when test="$mimetype='audio/mp3' or $mimetype='audio/x-wav' or $mimetype='audio/mpeg'">
					<div id="audioMp3">
						<audio width="100%" controls="controls">
							<xsl:attribute name="src"> 
								<xsl:value-of select="$src" /> 
							</xsl:attribute>
						</audio>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div id="mediaplayer" class="text-center"></div>
					<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
					<script type="text/javascript" src="http://p.jwpcdn.com/6/10/jwplayer.js"></script>
					<script type="text/javascript">
						$(document).ready(function(){        
							jwplayer('mediaplayer').setup({
								'playlist': [{
								  'sources': [
									{
									  'file': '<xsl:value-of select="$src"/>'
									}
								  ]
								}],
								'width': 450,
								'height': 320
							});
						});
					</script>
				</xsl:otherwise>
			</xsl:choose>


	</xsl:template>

</xsl:stylesheet>