<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://www.lyncode.com/xoai">
	<xsl:output indent="yes" method="xml"
		omit-xml-declaration="yes" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!-- Formatting dc.date.issued -->
	<xsl:template
		match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='issued']/doc:element/doc:field/text()">
		<xsl:call-template name="formatdate">
			<xsl:with-param name="datestr" select="." />
		</xsl:call-template>
	</xsl:template>
	<!-- Formatting dc.date.embargoEnd -->
	<xsl:template
		match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='embargoEnd']/doc:element/doc:field/text()">
		<xsl:call-template name="formatEmbargoDate">
			<xsl:with-param name="datestr" select="." />
		</xsl:call-template>
	</xsl:template>

	<!-- Replacing dc.type.hasVersion -->
	<xsl:template
		match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='hasVersion']/doc:element/doc:field/text()">
		<xsl:variable name="version" select="."/>
		<xsl:choose>
			<xsl:when test="$version='info:eu-repo/semantics/acceptedVersion'">
				<xsl:text>AM</xsl:text>
			</xsl:when>
			<xsl:when test="$version='info:eu-repo/semantics/publishedVersion'">
				<xsl:text>VoR</xsl:text>
			</xsl:when>
			<xsl:when test="$version='info:eu-repo/semantics/draft'">
				<xsl:text>AO</xsl:text>
			</xsl:when>
			<xsl:when test="$version='info:eu-repo/semantics/submittedVersion'">
				<xsl:text>SMUR</xsl:text>
			</xsl:when>
			<xsl:when test="$version='info:eu-repo/semantics/updatedVersion'">
				<xsl:text>CVoR</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>NA</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Reemplazo dc.rights.accesRights -->
	<xsl:template
		match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='accesRights']/doc:element/doc:field/text()">
		<xsl:variable name="right" select="."/>
		<xsl:choose>
			<xsl:when test="$right='info:eu-repo/semantics/restrictedAccess'">
				<xsl:text>Acceso restringido</xsl:text>
			</xsl:when>
			<xsl:when test="$right='info:eu-repo/semantics/embargoedAccess'">
				<xsl:text>Acceso embargado</xsl:text>
			</xsl:when>
			<xsl:when test="$right='info:eu-repo/semantics/closedAccess'">
				<xsl:text>Acceso cerrado</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Acceso abierto</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Reemplazo dc.type para seguir directrices COAR -->
	<xsl:template
		match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element/doc:field/text()">
		<xsl:variable name="type" select="."/>
		<xsl:choose>
			<xsl:when test="$type='article'">
				<xsl:text>Artículo de revista</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Article'">
				<xsl:text>Artículo de revista</xsl:text>
			</xsl:when>
			<xsl:when test="$type='bachelorThesis'">
				<xsl:text>Trabajo de grado de pregrado</xsl:text>
			</xsl:when>
			<xsl:when test="$type='book'">
				<xsl:text>Libro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='bookPart'">
				<xsl:text>Parte de libro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='conferenceObject'">
				<xsl:text>Objeto de conferencia</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Conference Paper'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='contributionToPeriodical'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='doctoralThesis'">
				<xsl:text>Tesis doctoral</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Erratum'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='lecture'">
				<xsl:text>Conferencia</xsl:text>
			</xsl:when>
			<xsl:when test="$type='masterThesis'">
				<xsl:text>Tesis de maestría</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Materialdeclase'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='other'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Other'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Ponencia'">
				<xsl:text>Conferencia</xsl:text>
			</xsl:when>
			<xsl:when test="$type='preprint'">
				<xsl:text>Preimpresión</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Presentación'">
				<xsl:text>Otro</xsl:text>
			</xsl:when>
			<xsl:when test="$type='report'">
				<xsl:text>Reporte técnico</xsl:text>
			</xsl:when>
			<xsl:when test="$type='review'">
				<xsl:text>Revisión</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Review'">
				<xsl:text>Revisión</xsl:text>
			</xsl:when>
			<xsl:when test="$type='workingPaper'">
				<xsl:text>Documento de trabajo</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- AUXILIARY TEMPLATES -->
	<!-- Date format -->
	<xsl:template name="formatdate">
		<xsl:param name="datestr" />
		<xsl:variable name="sub">
			<xsl:value-of select="substring($datestr,1,10)" />
		</xsl:variable>
		<xsl:value-of select="$sub" />
	</xsl:template>
	<xsl:template name="formatEmbargoDate">
		<xsl:param name="datestr" />
		<xsl:variable name="sub">
			<xsl:value-of
				select="substring-after($datestr,'info:eu-repo/date/embargoEnd/')" />
		</xsl:variable>
		<xsl:value-of select="$sub" />
	</xsl:template>
</xsl:stylesheet>
