<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://www.lyncode.com/xoai" version="1.0">
	<xsl:output omit-xml-declaration="yes" method="xml"
		indent="yes" />

	<xsl:template match="/">
		<resource xmlns="http://namespace.openaire.eu/schema/oaire"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:dcterms="http://purl.org/dc/terms/"
			xmlns:datacite="http://datacite.org/schema/kernel-4"
			xmlns:thesis="http://www.ndltd.org/standards/metadata/etd-ms-v1.1"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://namespace.openaire.eu/schema/oaire https://www.openaire.eu/schema/repo-lit/4.0/openaire.xsd">

			<datacite:titles>
				<!-- dc.title -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='title']/doc:element/doc:field[@name='value']">
					<datacite:title>
						<xsl:value-of select="." />
					</datacite:title>
				</xsl:for-each>
				<!-- dc.title.alternative -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='title']/doc:element[@name='alternative']/doc:element/doc:field[@name='value']">
					<datacite:title titleType="AlternativeTitle">
						<xsl:value-of select="." />
					</datacite:title>
				</xsl:for-each>
				<!-- dc.title.other -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='title']/doc:element[@name='other']/doc:element/doc:field[@name='value']">
					<datacite:title titleType="Other">
						<xsl:value-of select="." />
					</datacite:title>
				</xsl:for-each>
			</datacite:titles>

			<datacite:creators>
				<!-- dc.creator -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='creator']/doc:element/doc:field[@name='value']">
					<datacite:creator>
						<datacite:creatorName>
							<xsl:value-of select="." />
						</datacite:creatorName>
					</datacite:creator>
				</xsl:for-each>
				<!-- dc.contributor.author -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='author']/doc:element/doc:field[@name='value']">
					<datacite:creator>
						<datacite:creatorName>
							<xsl:value-of select="." />
						</datacite:creatorName>
					</datacite:creator>
				</xsl:for-each>
			</datacite:creators>

			<!-- datacite:contributors -->
			<datacite:contributors>
				<!-- dc.contributor.advisor -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='advisor']/doc:element/doc:field[@name='value']">
					<datacite:contributor
						contributorType="Advisor">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.other -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='other']/doc:element/doc:field[@name='value']">
					<datacite:contributor contributorType="Other">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.editor -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='editor']/doc:element/doc:field[@name='value']">
					<datacite:contributor
						contributorType="Editor">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.gruplac -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='gruplac']/doc:element/doc:field[@name='value']">
					<datacite:contributor
						contributorType="ResearchGroup">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.event -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='event']/doc:element/doc:field[@name='value']">
					<datacite:contributor
						contributorType="Producer">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.presenter -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='presenter']/doc:element/doc:field[@name='value']">
					<datacite:contributor contributorType="Other">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.sm -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='sm']/doc:element/doc:field[@name='value']">
					<datacite:contributor contributorType="Other">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor.grupline -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element[@name='grupline']/doc:element/doc:field[@name='value']">
					<datacite:contributor contributorType="Other">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
				<!-- dc.contributor -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='contributor']/doc:element/doc:field[@name='value']">
					<datacite:contributor contributorType="Other">
						<datacite:contributorName>
							<xsl:value-of select="." />
						</datacite:contributorName>
					</datacite:contributor>
				</xsl:for-each>
			</datacite:contributors>

			<!-- <fundingReferences> </fundingReferences> -->

			<!-- dc.identifier.uri -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='identifier']/doc:element[@name='uri']/doc:element/doc:field[@name='value']">
				<datacite:identifier identifierType="URI">
					<xsl:value-of select="." />
				</datacite:identifier>
			</xsl:for-each>

			<!-- datacite:alternateIdentifiers -->
			<datacite:alternateIdentifiers>
				<!-- dc.identifier.doi -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='identifier']/doc:element[@name='doi']/doc:element/doc:field[@name='value']">
					<datacite:alternateIdentifier
						alternateIdentifierType="DOI">
						<xsl:value-of select="." />
					</datacite:alternateIdentifier>
				</xsl:for-each>
				<!-- dc.identifier.isbn -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='identifier']/doc:element[@name='isbn']/doc:element/doc:field[@name='value']">
					<datacite:alternateIdentifier
						alternateIdentifierType="ISBN">
						<xsl:value-of select="." />
					</datacite:alternateIdentifier>
				</xsl:for-each>
			</datacite:alternateIdentifiers>

			<!-- datacite:relatedIdentifiers -->
			<datacite:relatedIdentifiers>
				<!-- dc.relation.uri -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='relation']/doc:element[@name='uri']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="URL" relationType="IsSourceOf">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
				<!-- dc.relation.ispartof -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='relation']/doc:element[@name='ispartof']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="OTHER" relationType="IsPartOf">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
				<!-- dc.relation.isversionof -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='relation']/doc:element[@name='isversionof']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="OTHER" relationType="IsVersionOf">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
				<!-- dc.relation.ispartofseries -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='relation']/doc:element[@name='ispartofseries']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="OTHER" relationType="IsPartOfSeries">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
				<!-- dc.relation.hasversion -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='relation']/doc:element[@name='hasversion']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="URL" relationType="HasVersion">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
				<!-- dc.relation.haspart -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='relation']/doc:element[@name='haspart']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="URL" relationType="HasPart">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
				<!-- dc.identifier.issn -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='identifier']/doc:element[@name='issn']/doc:element/doc:field[@name='value']">
					<datacite:relatedIdentifier
						relatedIdentifierType="ISSN" relationType="IsPartOfSeries">
						<xsl:value-of select="." />
					</datacite:relatedIdentifier>
				</xsl:for-each>
			</datacite:relatedIdentifiers>

			<datacite:dates>
				<xsl:choose>
					<xsl:when
						test="doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='issued']/doc:element/doc:field[@name='value']">
						<!-- dc.date.issued -->
						<xsl:for-each
							select="doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='issued']/doc:element/doc:field[@name='value']">
							<datacite:date dateType="Issued">
								<xsl:value-of select="." />
							</datacite:date>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<!-- dc.date.created -->
						<xsl:for-each
							select="doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='created']/doc:element/doc:field[@name='value']">
							<datacite:date dateType="Issued">
								<xsl:value-of select="." />
							</datacite:date>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
				<!-- dc.date.embargoEnd -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='embargoEnd']/doc:element/doc:field[@name='value']">
					<datacite:date dateType="Available">
						<xsl:value-of select="." />
					</datacite:date>
				</xsl:for-each>
			</datacite:dates>

			<!-- dc.language.iso -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='language']/doc:element[@name='iso']/doc:element/doc:field[@name='value']">
				<dc:language>
					<xsl:value-of select="." />
				</dc:language>
			</xsl:for-each>

			<!-- dc.publisher -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='publisher']/doc:element/doc:field[@name='value']">
				<dc:publisher>
					<xsl:value-of select="." />
				</dc:publisher>
			</xsl:for-each>

			<!-- dc.type -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element/doc:field[@name='value']">
				<resourceType resourceTypeContext="coar">
					<xsl:attribute name="uri">
						<xsl:call-template name="getTypeUri">
							<xsl:with-param name="type" select="." />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:value-of select="." />
				</resourceType>
			</xsl:for-each>
			<!-- dc.type.spa -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='spa']/doc:element/doc:field[@name='value']">
				<resourceType resourceTypeContext="local">
					<xsl:value-of select="." />
				</resourceType>
			</xsl:for-each>

			<!-- dc.type.hasversion -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='hasVersion']/doc:element/doc:field[@name='value']">
				<version>
					<xsl:attribute name="uri">
						<xsl:call-template name="getVersionUri">
							<xsl:with-param name="version" select="." />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:value-of select="." />
				</version>
			</xsl:for-each>

			<!-- dc.description -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='description']/doc:element/doc:field[@name='value']">
				<dc:description>
					<xsl:value-of select="." />
				</dc:description>
			</xsl:for-each>
			<!-- dc.description.* (not embargoEnd and not provenance) -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='description']/doc:element[@name!='embargoEnd' and @name!='provenance']/doc:element/doc:field[@name='value']">
				<dc:description>
					<xsl:attribute name="descriptionType">
						<xsl:variable name="qualifier" select="../../@name"/>
						<xsl:choose>
							<xsl:when test="$qualifier='abstract'">
								<xsl:text>abstract</xsl:text>
							</xsl:when>
							<xsl:when test="$qualifier='sponsorship'">
								<xsl:text>sponsorship</xsl:text>
							</xsl:when>
							<xsl:when test="$qualifier='tableofcontents'">
								<xsl:text>tableofcontents</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>other</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="." />
				</dc:description>
			</xsl:for-each>

			<!-- dc.format.mimetype -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='format']/doc:element[@name='mimetype']/doc:element/doc:field[@name='value']">
				<dc:format>
					<xsl:value-of select="." />
				</dc:format>
			</xsl:for-each>

			<!-- dc.rights.accesRights -->
			<datacite:rights>
				<xsl:attribute name="rightsURI">
					<xsl:call-template name="formatAccessRights">
						<xsl:with-param name="accessRight"
							select="doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='accesRights']/doc:element/doc:field[@name='value']" />
					</xsl:call-template>
				</xsl:attribute>
				<xsl:value-of
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='accesRights']/doc:element/doc:field[@name='value']" />
			</datacite:rights>

			<!-- dc.source -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='source']/doc:element/doc:field[@name='value']">
				<dc:source>
					<xsl:value-of select="." />
				</dc:source>
			</xsl:for-each>
			<!-- dc.source.* -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='source']/doc:element/doc:element/doc:field[@name='value']">
				<dc:source>
					<xsl:value-of select="." />
				</dc:source>
			</xsl:for-each>

			<!-- datacite:subjects -->
			<datacite:subjects>
				<!-- dc.subject -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='subject']/doc:element/doc:field[@name='value']">
					<datacite:subject subjectScheme="other">
						<xsl:value-of select="." />
					</datacite:subject>
				</xsl:for-each>
				<!-- dc.subject.* -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='subject']/doc:element/doc:element/doc:field[@name='value']">
					<datacite:subject>
						<xsl:attribute name="subjectScheme">
							<xsl:variable name="qualifier" select="../../@name"/>
							<xsl:choose>
								<xsl:when test="$qualifier='lemb'">
									<xsl:text>lemb</xsl:text>
								</xsl:when>
								<xsl:when test="$qualifier='ddc'">
									<xsl:text>ddc</xsl:text>
								</xsl:when>
								<xsl:when test="$qualifier='decs'">
									<xsl:text>decs</xsl:text>
								</xsl:when>
								<xsl:when test="$qualifier='classification'">
									<xsl:text>classification</xsl:text>
								</xsl:when>
								<xsl:when test="$qualifier='jel'">
									<xsl:text>jel</xsl:text>
								</xsl:when>
								<xsl:when test="$qualifier='mesh'">
									<xsl:text>mesh</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>other</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:value-of select="." />
					</datacite:subject>
				</xsl:for-each>
			</datacite:subjects>

			<!-- licenceCondition -->
			<xsl:choose>
				<xsl:when
					test="doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element/doc:field[@name='value']">
					<!-- dc.rights -->
					<xsl:for-each
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element/doc:field[@name='value']">
						<licenseCondition>
							<xsl:if
								test="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='uri']/doc:element/doc:field[@name='value']">
								<xsl:attribute name="uri">
									<xsl:value-of
									select="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='uri']/doc:element/doc:field[@name='value']" />
								</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="." />
						</licenseCondition>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<!-- dc.rights.cc -->
					<xsl:for-each
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='cc']/doc:element/doc:field[@name='value']">
						<licenseCondition>
							<xsl:if
								test="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='uri']/doc:element/doc:field[@name='value']">
								<xsl:attribute name="uri">
									<xsl:value-of
										select="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element[@name='uri']/doc:element/doc:field[@name='value']" />
								</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="." />
						</licenseCondition>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>

			<!-- dc.coverage -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='coverage']/doc:element/doc:field[@name='value']">
				<dc:coverage>
					<xsl:value-of select="." />
				</dc:coverage>
			</xsl:for-each>

			<!-- datasite:sizes -->
			<datacite:sizes>
				<!-- dc.format.extent -->
				<xsl:for-each
					select="doc:metadata/doc:element[@name='dc']/doc:element[@name='format']/doc:element[@name='extent']/doc:element/doc:field[@name='value']">
					<datacite:size>
						<xsl:value-of select="." />
					</datacite:size>
				</xsl:for-each>
			</datacite:sizes>

			<!-- <datacite:geoLocations> <datacite:geoLocation></datacite:geoLocation>
				</datacite:geoLocations> -->

			<!-- <file></file> -->

			<!-- <citationTitle></citationTitle> <citationVolume></citationVolume>
				<citationIssue></citationIssue> <citationStartPage></citationStartPage> <citationEndPage></citationEndPage>
				<citationEdition></citationEdition> <citationConferencePlace></citationConferencePlace>
				<citationConferenceDate></citationConferenceDate> -->

			<!-- dc.audience -->
			<xsl:for-each
				select="doc:metadata/doc:element[@name='dc']/doc:element[@name='audience']/doc:element/doc:field[@name='value']">
				<dcterms:audience>
					<xsl:value-of select="." />
				</dcterms:audience>
			</xsl:for-each>

			<!-- thesis.degree.* -->
			<xsl:if
				test="doc:metadata/doc:element[@name='dc']/doc:element[@name='creator']/doc:element[@name='degree']/doc:element/doc:field[@name='value']">
				<!-- dc.creator.degree -->
				<thesis.degree.name>
					<xsl:value-of
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='creator']/doc:element[@name='degree']/doc:element/doc:field[@name='value']" />
				</thesis.degree.name>

				<!-- dc.type.spa -->
				<thesis.degree.level>
					<xsl:value-of
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='spa']/doc:element/doc:field[@name='value']" />
				</thesis.degree.level>

				<!-- dc.publisher.department y dc.publisher.program -->
				<thesis.degree.discipline>
					<xsl:value-of
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='publisher']/doc:element[@name='department']/doc:element/doc:field[@name='value']"
					/>. <xsl:value-of
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='publisher']/doc:element[@name='program']/doc:element/doc:field[@name='value']" />
				</thesis.degree.discipline>

				<!-- dc.publisher -->
				<thesis.degree.grantor>
					<xsl:value-of
						select="doc:metadata/doc:element[@name='dc']/doc:element[@name='publisher']/doc:element/doc:field[@name='value']" />
				</thesis.degree.grantor>
			</xsl:if>

		</resource>
	</xsl:template>

	<!-- AUXILIARY TEMPLATES -->
	<!-- Replacing dc.rights.accessRights -->
	<xsl:template name="formatAccessRights">
		<xsl:param name="accessRight" />
		<xsl:choose>
			<xsl:when test="$accessRight='Acceso restringido'">
				<xsl:text>http://purl.org/coar/access_right/c_16ec</xsl:text>
			</xsl:when>
			<xsl:when test="$accessRight='Acceso embargado'">
				<xsl:text>http://purl.org/coar/access_right/c_f1cf</xsl:text>
			</xsl:when>
			<xsl:when test="$accessRight='Acceso cerrado'">
				<xsl:text>http://purl.org/coar/access_right/c_14cb</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>http://purl.org/coar/access_right/c_abf2</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getTypeUri">
		<xsl:param name="type" />
		<xsl:choose>
			<xsl:when test="$type='Artículo de revista'">
				<xsl:text>http://purl.org/coar/resource_type/c_6501</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Artículo'">
				<xsl:text>http://purl.org/coar/resource_type/c_6501</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Trabajo de grado de pregrado'">
				<xsl:text>http://purl.org/coar/resource_type/c_7a1f</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Libro'">
				<xsl:text>http://purl.org/coar/resource_type/c_2f33</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Parte de libro'">
				<xsl:text>http://purl.org/coar/resource_type/c_3248</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Objeto de conferencia'">
				<xsl:text>http://purl.org/coar/resource_type/c_c94f</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Tesis doctoral'">
				<xsl:text>http://purl.org/coar/resource_type/c_db06</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Conferencia'">
				<xsl:text>http://purl.org/coar/resource_type/c_8544</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Tesis de maestría'">
				<xsl:text>http://purl.org/coar/resource_type/c_bdcc</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Preimpresión'">
				<xsl:text>http://purl.org/coar/resource_type/c_816b</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Reporte técnico'">
				<xsl:text>http://purl.org/coar/resource_type/c_18gh</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Revisión'">
				<xsl:text>http://purl.org/coar/resource_type/c_efa0</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Video'">
				<xsl:text>http://purl.org/coar/resource_type/c_12ce</xsl:text>
			</xsl:when>
			<xsl:when test="$type='Documento de trabajo'">
				<xsl:text>http://purl.org/coar/resource_type/c_8042</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>http://purl.org/coar/resource_type/c_1843</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getVersionUri">
		<xsl:variable name="version" select="." />
		<xsl:choose>
			<xsl:when test="$version='AM'">
				<xsl:text>http://purl.org/coar/version/c_ab4af688f83e57aa</xsl:text>
			</xsl:when>
			<xsl:when test="$version='VoR'">
				<xsl:text>http://purl.org/coar/version/c_970fb48d4fbd8a85</xsl:text>
			</xsl:when>
			<xsl:when test="$version='AO'">
				<xsl:text>http://purl.org/coar/version/c_b1a7d7d4d402bcce</xsl:text>
			</xsl:when>
			<xsl:when test="$version='CVoR'">
				<xsl:text>http://purl.org/coar/version/c_e19f295774971610</xsl:text>
			</xsl:when>
			<xsl:when test="$version='SMUR'">
				<xsl:text>http://purl.org/coar/version/c_71e4c1898caa6e32</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>http://purl.org/coar/version/c_be7fb7dd8ff6fe43</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
