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
	xmlns:util="org.dspace.app.xmlui.utils.XSLUtils" >

	<xsl:output indent="yes" />

 <!--  Comienzan cambios con las estadisticas  -->
	
	<xsl:template name="meses">
		<xsl:variable name="labels" select="node()/dri:row[1]" />
		 <xsl:for-each select="$labels/dri:cell">
			<xsl:variable name="pos" select="position()" />
			<xsl:if test="$pos != 1">
				|<xsl:value-of select="node()"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="valores">
		<xsl:variable name="valores" select="node()/dri:row[2]" />
		 <xsl:for-each select="$valores/dri:cell">
			<xsl:variable name="pos" select="position()" />
			<xsl:if test="$pos != 1">
				<xsl:value-of select="node()"/>
				<xsl:if test="count(following-sibling::node()) != 0">
				  <xsl:text>,</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	
	
	
    <xsl:template match="dri:div[@id='aspect.statistics.ExtendedStatisticsTransformer.div.stats']/dri:table | dri:div[@id='aspect.statistics.ExtendedStatisticsTransformer.div.stats']/dri:div[@id='aspect.statistics.ExtendedStatisticsTransformer.div.tablewrapper']">
		<script type="text/javascript" src="https://www.google.com/jsapi">;</script>
		<script type="text/javascript">
			<xsl:attribute name="src">
				<xsl:value-of select="$context-path"/>
				<xsl:text>/themes/Mirage2/xsl/aspect/estadisticas/js/jquery-1.6.2.min.js</xsl:text>
			</xsl:attribute>
		</script>
		<script type="text/javascript">
			<xsl:attribute name="src">
				<xsl:value-of select="$context-path"/>
				<xsl:text>/themes/Mirage2/xsl/aspect/estadisticas/js/jquery-1.6.2.min.js</xsl:text>
			</xsl:attribute>
		</script>
		<link rel="stylesheet" href="/themes/Mirage2/xsl/aspect/estadisticas/style_estadisticas.css"/>
		<xsl:choose>
		<!-- Tabla de visitas mensualizada -->
			<xsl:when test="node()/@id='aspect.statistics.ExtendedStatisticsTransformer.table.list-table'">
				<xsl:variable name="labels" select="node()/dri:row[1]" />
				<xsl:variable name="datas" select="node()/dri:row[2]" />
				<xsl:variable name="link" select="//@OBJID" />
				<xsl:variable name="url" select="substring-before($link,'handle')" />
						
				
				<script type="text/javascript">
					  google.load("visualization", "1", {packages:["corechart"]});
					  google.setOnLoadCallback(drawVisualizationGrafica2);
					  function drawVisualizationGrafica2() {
					  var array=([
							  ['Year', '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>']
							  <xsl:for-each select="$labels/dri:cell">
								<xsl:variable name="pos" select="position()" />
								<xsl:if test="$pos != 1">
									<xsl:text>, </xsl:text>
									['<xsl:value-of select="node()"/>', <xsl:value-of select="$datas/dri:cell[$pos]"/>]
								</xsl:if>
							</xsl:for-each>
							]);
							array = array.slice(0,-1);
							var data = google.visualization.arrayToDataTable(array);
							
							var w = window,
						    d = document,
						    e = d.documentElement,
						    g = d.getElementsByTagName('body')[0],
						    x = w.innerWidth || e.clientWidth || g.clientWidth,
						    y = w.innerHeight|| e.clientHeight|| g.clientHeight;
							
							var options;
							
							if(x&lt;=400){
							
								options = {
								  width: x,
								  height: y*3/4,   
								  title: '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>',
								  legend: { position: 'bottom' },
								  
								  colors: ['#607890']
								};
							}
							else{
								options = {
								  width: x*5/9,
								  height: y*3/4,
								  title: '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>',
								  legend: { position: 'bottom' },
								  
								  colors: ['#607890']
								};
							
							}

							<!-- var chart = new google.visualization.LineChart(document.getElementById('grafico2Lineas')); -->
							var chart2 = new google.visualization.ColumnChart(document.getElementById('grafico2Barras'));
							<!-- chart.draw(data, options);  -->
							chart2.draw(data, options);
							rellenaCsv(array);
						}
						 function rellenaCsv(array) {
						 	jQuery(document).ready(function(){
						 		jQuery("#csvDownload").attr("href","<xsl:value-of select="$context-path"/>".concat("/csv?data=".concat(escape(array.join()))));
						 	})
						 }

						<!-- jQuery(document).ready(function(){
							  jQuery("#imgGraficoLineas").click(function(){
								jQuery("#grafico2LineasDiv").show();
								jQuery("#grafico2BarrasDiv").hide();
								drawVisualizationGrafica2() ;
							  });
							   jQuery("#imgGraficoBarras").click(function(){
								jQuery("#grafico2BarrasDiv").show();
								jQuery("#grafico2LineasDiv").hide();
								drawVisualizationGrafica2() 
							  });
							});
							jQuery( window ).load(function() {
								jQuery("#grafico2BarrasDiv").hide();
							}); -->
				 </script>
				 <div class="modoVisitas">
				 <h3 class="ds-table-head"><i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text></h3>
				 <div id="tabla_visitas_temporal" class="ds-table tableWithTitle detailtable">
	
	
	
					<table class="table table-striped table-hover ">
					<xsl:variable name="pos" select="position()" />
					<thead>
						<tr>
							<th></th>
							<th class="statistics_value statistics_header_view">
								<i18n:text>
									<xsl:value-of select="dri:table/dri:head"/>
								</i18n:text>							
							</th>
						</tr>
					</thead>
					<tbody>
						<xsl:for-each select="$labels/dri:cell">
							<xsl:variable name="pos" select="position()" />
							<xsl:if test="$pos != 1">
								<tr>
									<td>
										<xsl:value-of select="node()" />
									</td>
									<td class="statistics_value">
										<xsl:value-of select="$datas/dri:cell[$pos]" />
									</td>
								</tr>
							</xsl:if>
				
						</xsl:for-each>
				
						<tr>
							<td></td>
							<td class="statistics_value">
								<a id="csvDownload">
									<xsl:attribute name="class">
											<xsl:text>btn btn-primary</xsl:text>
									</xsl:attribute>
									<xsl:attribute name="alt">
										<xsl:text>xmlui.estadisticas.imageCsv.alt</xsl:text>
									</xsl:attribute>
									<xsl:attribute name="i18n:attr">
										<xsl:text>alt</xsl:text>
									</xsl:attribute>
									<i18n:text>xmlui.statistics.csv-download-button</i18n:text>
									<span class="glyphicon glyphicon-download-alt"></span>
								</a>
							</td>
						</tr>
				
					</tbody>
				</table> 
				
				</div>
								 
			<!--
				 <div class="graficoEstadistico" id="grafico2LineasDiv"><span id="grafico2Lineas">&#160;</span>
				 <img>
					<xsl:attribute name="id">
						<xsl:text>imgGraficoBarras</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="src">
						<xsl:value-of select="$context-path"/>
						<xsl:text>/themes/Mirage2/xsl/aspect/estadisticas/images/graficoBarras_</xsl:text>
						<xsl:value-of select="//dri:metadata[@element='locale'][@qualifier='ISO639-1']"/>
						<xsl:text>.png</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:text>xmlui.estadisticas.graficoBarras.alt</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="i18n:attr">
						<xsl:text>alt</xsl:text>
					</xsl:attribute>
				</img>
				</div>
			 -->
				 <div class="graficoEstadistico" id="grafico2BarrasDiv"><span  id="grafico2Barras">&#160;</span>
				 <!--
				 <img>
					<xsl:attribute name="id">
						<xsl:text>imgGraficoLineas</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="src">
						<xsl:value-of select="$context-path"/>
						<xsl:text>/themes/Mirage2/xsl/aspect/estadisticas/images/graficoLineas_</xsl:text>
						<xsl:value-of select="//dri:metadata[@element='locale'][@qualifier='ISO639-1']"/>
						<xsl:text>.png</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:text>xmlui.estadisticas.graficoLineas.alt</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="i18n:attr">
						<xsl:text>alt</xsl:text>
					</xsl:attribute>
				</img>
				 -->
				</div>
				 </div>
			</xsl:when>
			<!-- Visitas por pais -->
			<xsl:when test="@id='aspect.statistics.ExtendedStatisticsTransformer.table.list-country'">
			 <div class="modoVisitas">

				<script type="text/javascript">
						 <!-- Gráfico geof -->
						 google.load('visualization', '1', {'packages': ['geochart']});
						 <!-- Gráfico de torta -->
						google.load('visualization',1, {'packages':['corechart']});
						google.setOnLoadCallback(drawMundial);

						  function drawMundial() {
							var data = google.visualization.arrayToDataTable([
							  <xsl:for-each select="dri:row">
								<xsl:variable name="pos" select="position()" />
								<xsl:if test="$pos = 1">
									['Country', '<i18n:text><xsl:value-of select="dri:cell[2]"/></i18n:text>']
								</xsl:if>
								<xsl:if test="$pos != 1">
									<xsl:text>, </xsl:text>
									['<xsl:call-template name="string-replace-all">
						    <xsl:with-param name="text" select="dri:cell[1]"/>
						    <xsl:with-param name="replace" select="'Ã±'"/>
						    <xsl:with-param name="by" select="'n'"/>
						</xsl:call-template>', <xsl:value-of select="dri:cell[2]"/>]
								</xsl:if>
							</xsl:for-each>
							]);
							
							
							var w = window,
						    d = document,
						    e = d.documentElement,
						    g = d.getElementsByTagName('body')[0],
						    x = w.innerWidth || e.clientWidth || g.clientWidth,
						    y = w.innerHeight|| e.clientHeight|| g.clientHeight;
							
							
							if(x&lt;=400){
							
								optionsGeoMap = {
								  width: x*6/8,   
								  height: y*3/4,   
								  title: '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>',
								  legend: { position: 'bottom' },
								  
								  colorAxis: {colors: ['#ffffff','#607890']}
								};
								optionsPieChart = {
								  width: x*10/2,
								  height: y*10/2,
								  legend: { position: 'bottom' },
								};
							}
							else{
								optionsGeoMap = {
								  width: x/1.8,
								  height: y*3/4,
								  title: '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>',
								  legend: { position: 'bottom' },
								  
								  colorAxis: {colors: ['#ffffff','#607890']}
								};
								optionsPieChart = {
								  width: x/1.8,
								  height: y,
								  legend: { alignment: 'center', position: 'top', maxLines:0 },
								};
							
							}

							var chart = new google.visualization.GeoChart(document.getElementById('grafico_paises'));
							chart.draw(data, optionsGeoMap);

							var chart = new google.visualization.PieChart(document.getElementById('grafico_paises_torta'));
							chart.draw(data, optionsPieChart);
						};

					  </script>
				 <h3 class="ds-table-head"><i18n:text><xsl:value-of select="dri:head"/></i18n:text></h3>
				 <div id="tabla_paises" class="ds-table tableWithTitle detailtable">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th>
								</th>
								<th class="statistics_value statistics_header_view">
									<i18n:text>
										<xsl:value-of select="dri:row[1]/dri:cell[2]"/>
									</i18n:text>
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="dri:row">
								<xsl:variable name="pos" select="position()" />
								<xsl:if test="$pos != 1">
									<tr>
										<td>
											 <xsl:value-of select="util:getCountryName(dri:cell[1],/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='currentLocale'])" />
										</td>
										<td class="statistics_value">
											<xsl:value-of select="dri:cell[2]" />
										</td>
									</tr>
								</xsl:if>

							</xsl:for-each>

						</tbody>
					</table>

				</div>

				<div id="grafico_paises_torta" class="graficoEstadistico">&#160;</div>
				
				<div id="grafico_paises" class="graficoEstadistico" >&#160;</div>
			 </div>
			</xsl:when>
			<!-- Visitas por ciudades -->
			 <xsl:when test="@id='aspect.statistics.ExtendedStatisticsTransformer.table.list3'">
			<!-- <div class="modoVisitas">
			<xsl:copy>
	                <xsl:apply-templates select="node()|@*"/>
			</xsl:copy>
				<script type='text/javascript'>
			 google.load('visualization', '1', {'packages': ['geochart']});
			 google.setOnLoadCallback(drawCiudadesMap);

			  function drawCiudadesMap() {
			  var data = google.visualization.arrayToDataTable([
			   <xsl:for-each select="dri:row">
					<xsl:variable name="pos" select="position()" />
					<xsl:if test="$pos = 1">
						['City', '<i18n:text><xsl:value-of select="dri:cell[2]"/></i18n:text>']
					</xsl:if>
					<xsl:if test="$pos != 1">
						<xsl:text>, </xsl:text>
						['<xsl:call-template name="string-replace-all">
						    <xsl:with-param name="text" select="dri:cell[1]"/>
						    <xsl:with-param name="replace" select="'Ã±'"/>
						    <xsl:with-param name="by" select="'n'"/>
						</xsl:call-template>', <xsl:value-of select="dri:cell[2]"/>]
					</xsl:if>
				</xsl:for-each>
				]);
				
				var w = window,
						    d = document,
						    e = d.documentElement,
						    g = d.getElementsByTagName('body')[0],
						    x = w.innerWidth || e.clientWidth || g.clientWidth,
						    y = w.innerHeight|| e.clientHeight|| g.clientHeight;
				
			  if(x&lt;=400){
							
								options = {
								  width: x*6/8,   
								  height: y*3/4,   
								  title: '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>',
								  legend: { position: 'bottom' },
								  displayMode: 'markers',
								  colorAxis: {colors: ['#ffffff','#ad3125']}
								};
							}
							else{
								options = {
								  width: x*5/9,   
								  height: y*3/4,   
								  title: '<i18n:text><xsl:value-of select="node()/dri:head"/></i18n:text>',
								  legend: { position: 'bottom' },
								  displayMode: 'markers',
								  colorAxis: {colors: ['#ffffff','#ad3125']}
								};
							
							}

			  var chart = new google.visualization.GeoChart(document.getElementById('grafico_ciudades'));
			  chart.draw(data, options);
			};
			</script>
		 	  
	     		<div id="grafico_ciudades" class="graficoEstadistico">&#160;</div>
	    </div>  -->
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
	                <xsl:apply-templates select="node()|@*"/>
			</xsl:copy>
			</xsl:otherwise>
      </xsl:choose>
            
                
        </xsl:template>  
 
       <!--  <xsl:template match="dri:field[@id='aspect.statistics.ExtendedStatisticsTransformer.field.time_filter']">
     <span><xsl:value-of select="label"/></span>
    	<xsl:copy>
	                <xsl:apply-templates select="parent()|@*"/>
			</xsl:copy>
     	<spam id="seleccionModoVisitas"><i18n:text>xmlui.statistics.modo.visitas</i18n:text></spam><spam id="seleccionModoDescargas"><i18n:text>xmlui.statistics.modo.visitas</i18n:text></spam> 
    </xsl:template>-->
	<xsl:template match="*[@id='aspect.statistics.ExtendedStatisticsTransformer.p.object_filter_para']">
	    <xsl:copy>
	        <xsl:apply-templates select="node()|@*"/>
	    </xsl:copy>
	</xsl:template>
       <xsl:template match="*[@id='aspect.statistics.ExtendedStatisticsTransformer.p.time_filter_para']">
    	<xsl:copy>
	                <xsl:apply-templates select="node()|@*"/>
			</xsl:copy>
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