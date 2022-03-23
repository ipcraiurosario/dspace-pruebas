<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:dim="http://www.dspace.org/xmlns/dspace/dim" xmlns:xlink="http://www.w3.org/TR/xlink/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:atom="http://www.w3.org/2005/Atom" xmlns:ore="http://www.openarchives.org/ore/terms/"
	xmlns:oreatom="http://www.openarchives.org/ore/atom/" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xalan="http://xml.apache.org/xalan" xmlns:encoder="xalan://java.net.URLEncoder"
	xmlns:util="org.dspace.app.xmlui.utils.XSLUtils" xmlns:jstring="java.lang.String"
	xmlns:orcidutils="org.dspace.content.authority.OrcidUtils"
	xmlns:subjectauth="es.arvo.subject.authority.ArvoSubjectAuthority"
	xmlns:confman="org.dspace.core.ConfigurationManager" xmlns:rights="http://cosimo.stanford.edu/sdr/metsrights/"
	exclude-result-prefixes="xalan encoder i18n dri mets dim xlink xsl util jstring rights">

	<xsl:output indent="yes" />
	<!-- Ponemos el enlace orcid en el listado de autores -->
	<xsl:template
		match="dri:div[@id='aspect.artifactbrowser.ConfigurableBrowse.div.browse-by-author-results']/dri:table[@id='aspect.artifactbrowser.ConfigurableBrowse.table.browse-by-author-results']//dri:xref">
		<xsl:param name="idAutor"
			select="substring-before(substring-after(@target,'authority='),'&amp;type=author')" />
		<xsl:param name="idOrcid" select="orcidutils:getIdOrcid($idAutor)" />
		<xsl:param name="urlOrcid" select="confman:getProperty('url.orcid')" />
		<xsl:param name="idScholar" select="orcidutils:getIdScholar($idAutor)" />
		<xsl:param name="idScopus" select="orcidutils:getIdScopus($idAutor)" />
		<xsl:param name="idResearcher" select="orcidutils:getIdResearcher($idAutor)" />
		<xsl:param name="idCvlac" select="orcidutils:getCvlac($idAutor)"/>
		<xsl:param name="idPlumx" select="orcidutils:getPlumx($idAutor)"/>
		<xsl:param name="idPivot" select="orcidutils:getPivot($idAutor)"/>

		<a>
			<xsl:if test="@target">
				<xsl:attribute name="href"><xsl:value-of select="@target" /></xsl:attribute>
			</xsl:if>

			<xsl:if test="@rend">
				<xsl:attribute name="class"><xsl:value-of select="@rend" /></xsl:attribute>
			</xsl:if>

			<xsl:if test="@n">
				<xsl:attribute name="name"><xsl:value-of select="@n" /></xsl:attribute>
			</xsl:if>

			<xsl:apply-templates />
		</a>

		<xsl:if test="$idAutor and string-length($idAutor) &lt; 30">
			<a>
                                <xsl:attribute name="target">_blank</xsl:attribute>
                                <xsl:attribute name="class"><xsl:text>enlace_hub</xsl:text></xsl:attribute>
                                <xsl:attribute name="href">
                                    <xsl:text>http://research-hub.urosario.edu.co/individual/n</xsl:text>
                                    <xsl:value-of select="$idAutor" />
                                </xsl:attribute>
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
                        </a>
		</xsl:if>

		<xsl:if test="translate($idOrcid,' ','')">
			<xsl:text> </xsl:text>
			<a>
				<xsl:attribute name="target">_blank</xsl:attribute>
				<xsl:attribute name="href"><xsl:value-of select="$urlOrcid" /><xsl:value-of
					select="$idOrcid" /></xsl:attribute>
				<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
				<img>
					<xsl:attribute name="alt"><xsl:text>Orcid</xsl:text></xsl:attribute>
					<xsl:attribute name="title"><xsl:text>Orcid</xsl:text></xsl:attribute>
					<xsl:attribute name="src">
                    	<xsl:value-of select="$context-path" />
                    	<xsl:text>/themes/Mirage2/images/autoridades/orcid.png</xsl:text>
                    </xsl:attribute>
				</img>
			</a>
		</xsl:if>

		<xsl:if test="translate($idScholar,' ','')">
			<xsl:text> </xsl:text>
			<a>
				<xsl:attribute name="target">_blank</xsl:attribute>
				<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
				<xsl:attribute name="href">
        			<xsl:text>http://scholar.google.es/citations?user=</xsl:text>
        			<xsl:value-of select="$idScholar" />
        		</xsl:attribute>
				<img>
					<xsl:attribute name="alt"><xsl:text>Google Scholar</xsl:text></xsl:attribute>
					<xsl:attribute name="title"><xsl:text>Google Scholar</xsl:text></xsl:attribute>
					<xsl:attribute name="src">
                    	<xsl:value-of select="$context-path" />
                    	<xsl:text>/themes/Mirage2/images/autoridades/scholar.png</xsl:text>
                    </xsl:attribute>
				</img>
			</a>
		</xsl:if>

		<xsl:if test="translate($idScopus,' ','')">
			<xsl:text> </xsl:text>
			<a>
				<xsl:attribute name="target">_blank</xsl:attribute>
				<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
				<xsl:attribute name="href">
        			<xsl:text>http://www.scopus.com/authid/detail.url?authorId=</xsl:text>
        			<xsl:value-of select="$idScopus" />
        		</xsl:attribute>
				<img>
					<xsl:attribute name="alt"><xsl:text>Scopus</xsl:text></xsl:attribute>
					<xsl:attribute name="title"><xsl:text>Scopus</xsl:text></xsl:attribute>
					<xsl:attribute name="src">
                    	<xsl:value-of select="$context-path" />
                    	<xsl:text>/themes/Mirage2/images/autoridades/scopus.png</xsl:text>
                    </xsl:attribute>
				</img>
			</a>
		</xsl:if>

		<xsl:if test="translate($idResearcher,' ','')">
			<xsl:text> </xsl:text>
			<a>
				<xsl:attribute name="target">_blank</xsl:attribute>
				<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
				<xsl:attribute name="href">
        			<xsl:text>http://www.researcherid.com/rid/</xsl:text>
        			<xsl:value-of select="$idResearcher" />
        		</xsl:attribute>
				<img style="">
					<xsl:attribute name="alt"><xsl:text>ResearcherID</xsl:text></xsl:attribute>
					<xsl:attribute name="title"><xsl:text>ResearcherID</xsl:text></xsl:attribute>
					<xsl:attribute name="src">
                    	<xsl:value-of select="$context-path" />
                    	<xsl:text>/themes/Mirage2/images/autoridades/researcher.gif</xsl:text>
                    </xsl:attribute>
				</img>
			</a>
		</xsl:if>
		
		<xsl:if test="translate($idPlumx,' ','')">
        	<xsl:text> </xsl:text>
        	<a>
        		<xsl:attribute name="target">_blank</xsl:attribute>
        		<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
        		<xsl:attribute name="href">
        			<xsl:text>https://plu.mx/urosario/u/</xsl:text>
        			<xsl:value-of select="$idPlumx"/>
        		</xsl:attribute>
		        <img>
		           <xsl:attribute name="alt"><xsl:text>Plumx</xsl:text></xsl:attribute>				   
				   <xsl:attribute name="title"><xsl:text>Plumx</xsl:text></xsl:attribute>
				     <xsl:attribute name="src">
                    	<xsl:value-of select="$context-path"/>
                    	<xsl:text>/themes/Mirage2/images/autoridades/plumx.ico</xsl:text>
                    </xsl:attribute>
				</img>
			</a>
		</xsl:if>
		
		<xsl:if test="translate($idCvlac,' ','')">
        	<xsl:text> </xsl:text>
        	<a>
        		<xsl:attribute name="target">_blank</xsl:attribute>
        		<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
        		<xsl:attribute name="href">
        			<xsl:text>http://scienti.colciencias.gov.co:8081/cvlac/visualizador/generarCurriculoCv.do?cod_rh=</xsl:text>
        			<xsl:value-of select="$idCvlac"/>
        		</xsl:attribute>
		        <img>
		           <xsl:attribute name="alt"><xsl:text>Cvlac</xsl:text></xsl:attribute>				   
				   <xsl:attribute name="title"><xsl:text>Cvlac</xsl:text></xsl:attribute>
				     <xsl:attribute name="src">
                    	<xsl:value-of select="$context-path"/>
                    	<xsl:text>/themes/Mirage2/images/autoridades/cvlac.ico</xsl:text>
                    </xsl:attribute>
				</img>
			</a>
		</xsl:if>
		
		<xsl:if test="translate($idPivot,' ','')">
        	<xsl:text> </xsl:text>
        	<!--<a>
        		<xsl:attribute name="target">_blank</xsl:attribute>
        		<xsl:attribute name="class"><xsl:text>enlace_orcid</xsl:text></xsl:attribute>
        		<xsl:attribute name="href">
        			<xsl:text>http://pivot.cos.com/profiles/</xsl:text>
        			<xsl:value-of select="$idPivot"/>
        		</xsl:attribute>
		        <img>
		           <xsl:attribute name="alt"><xsl:text>Pivot</xsl:text></xsl:attribute>				   
				   <xsl:attribute name="title"><xsl:text>Pivot</xsl:text></xsl:attribute>
				     <xsl:attribute name="src">
                    	<xsl:value-of select="$context-path"/>
                    	<xsl:text>/themes/Mirage2/images/autoridades/pivot.ico</xsl:text>
                    </xsl:attribute>
				</img>
			</a>-->
		</xsl:if>
		
	</xsl:template>

</xsl:stylesheet>
