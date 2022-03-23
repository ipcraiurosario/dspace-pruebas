/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.statistics;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.statistics.Dataset;
import org.dspace.statistics.content.*;
import org.dspace.statistics.content.filter.StatisticsSolrDateFilter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public class ExtendedStatisticsTransformer extends AbstractStatisticsDataTransformer {

    private static Logger log = Logger.getLogger(ExtendedStatisticsTransformer.class);

    private static final Message T_dspace_home = message("xmlui.general.dspace_home");
    private static final Message T_head_dso = message("xmlui.statistics.head-dso");
    private static final Message T_head_title = message("xmlui.statistics.title");
    private static final String T_head_statistics = "xmlui.statistics.head.";
    private static final String T_head_countries = "xmlui.statistics.countries.";
    private static final String T_head_cities = "xmlui.statistics.cities.";
    private static final String T_statistics_any_views = "any-views";
    private static final String T_statistics_item_views = "item-views";
    private static final String T_statistics_items_views = "items-views";
    private static final String T_statistics_downloads = "downloads";
    private static final Message T_statistics_trail = message("xmlui.statistics.trail");
    private static final String T_head_visits_views = "xmlui.statistics.visits.views";
    private static final String T_head_downloads_bitstream = "xmlui.statistics.downloads.bitstreams";

    private static final String T_head_top_views_home = "xmlui.statistics.top.views.home";
    private static final String T_head_top_downloads_home = "xmlui.statistics.top.downloads.home";
    
    private static final Message T_no_results = message("xmlui.statistics.workflow.no-results");
    private static final Message T_time_filter_year = message("xmlui.statistics.StatisticsSearchTransformer.time-filter.year");
    private static final Message T_time_filter_month = message("xmlui.statistics.StatisticsSearchTransformer.time-filter.month");

    private static final String T_head_temporal_bitstream = "xmlui.statistics.bitstream.temporal";
    private static final String T_time_filter_seleccione = "xmlui.statistics.time_filter.seleccione";
    
    private static final String T_statistics_type_filter_title="xmlui.statistics.type.filter.title";
    private static final Message T_statistics_type_filter_download= message("xmlui.statistics.type.filter.download");
    private static final Message T_statistics_type_filter_item_view= message("xmlui.statistics.type.filter.item-view");
    private static final Message T_statistics_type_filter_items_views= message("xmlui.statistics.type.filter.items-views");
    private static final Message T_statistics_type_filter_any_view= message("xmlui.statistics.type.filter.any-view");
    private static final Message T_general_search = message("xmlui.general.search");
        
    protected final ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    
    /**
     * Add a page title and trail links
     */
    public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
	//Try to find our dspace object
	DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

	pageMeta.addTrailLink(contextPath + "/",T_dspace_home);

	if(dso != null)
	{
	    HandleUtil.buildHandleTrail(context,dso, pageMeta, contextPath, true);
	}
	pageMeta.addTrailLink(contextPath + "/handle" + (dso != null && dso.getHandle() != null ? "/" + dso.getHandle() : "/statistics"), T_statistics_trail);

	// Add the page title
	pageMeta.addMetadata("title").addContent(T_head_title);
    }

    /**
     * What to add at the end of the body
     */
	public void addBody(Body body)
			throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {

		// Try to find our dspace object
		DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

		StringBuilder actionPath = new StringBuilder().append(contextPath);

		try {
			// First get parameters from user
			Request request = ObjectModelHelper.getRequest(objectModel);
			String selectedTimeFilter = request.getParameter("time_filter");
			int selectedObjectFilter = (request.getParameter("object_filter") == null) ? -1
					: Integer.parseInt(request.getParameter("object_filter"));
			if (StringUtils.isBlank(selectedTimeFilter)) {
				selectedTimeFilter = "-12m";
			}
			String statisticsType;
			if (selectedObjectFilter == 0) {
				selectedObjectFilter = Constants.BITSTREAM;
				statisticsType = T_statistics_downloads;
			} else if (selectedObjectFilter == 1) {
				selectedObjectFilter = Constants.ITEM;
				if (dso != null && dso instanceof org.dspace.content.Item) {
				    statisticsType = T_statistics_item_views;
				}
				else {
				    statisticsType = T_statistics_items_views;
				}
			} else {
				selectedObjectFilter = -1;
				statisticsType = T_statistics_any_views;
			}
			Division mainDivision = body.addInteractiveDivision("statistics", actionPath.toString(),
					Division.METHOD_POST, null);

			// Build the collection viewer division.
			Division division = mainDivision.addDivision("stats", "secondary stats");

			// Add the time filter box
			addTimeFilter(division);
			// Add the access type filter
			addObjectFilter(division, dso);
			//Add search button
			addButton(division);

			if (dso != null) {
				actionPath.append("/handle/").append(dso.getHandle());
				actionPath.append("/statistics");
				renderViewer(division, dso, selectedTimeFilter, selectedObjectFilter, statisticsType);
			} else {
				actionPath.append("/statistics-home");
				renderHome(division, selectedTimeFilter, selectedObjectFilter, statisticsType);
			}

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

	public void renderHome(Division division, String selectedTimeFilter, int selectedObjectFilter, String statisticsType) throws WingException {

		division.setHead(T_head_title);

		// Retrieve the optional time filter
		StatisticsSolrDateFilter dateFilter = getDateFilter(selectedTimeFilter);

		// Visitas por mes o por año
		try {

			StatisticsTable statisticsTable = new StatisticsTable(new StatisticsDataVisits());

			statisticsTable.setTitle(T_head_statistics + statisticsType);
			statisticsTable.setId("tab1");

			DatasetTimeGenerator timeAxis = new DatasetTimeGenerator();
			timeAxis.setDateInterval(obtenerTipo(selectedTimeFilter), obtenerInicio(selectedTimeFilter), "+1");
			timeAxis.setIncludeTotal(true);
			statisticsTable.addDatasetGenerator(timeAxis);

			DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
			dsoAxis.addDsoChild(selectedObjectFilter, -1, false, -1);
			statisticsTable.addDatasetGenerator(dsoAxis);

			addDisplayTableExtended(division, statisticsTable, T_head_visits_views);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error occurred while creating home statistics", e);
		}

		// Paises con mas visualizaciones
		try {
			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits());

			statListing.setTitle(T_head_countries + statisticsType);
			statListing.setId("list-country");

			DatasetTypeGenerator typeAxis = new DatasetTypeGenerator();
			typeAxis.setType("countryCode");
			typeAxis.setSearchDsoType(selectedObjectFilter);
			typeAxis.setMax(10);
			typeAxis.setIncludeTotal(true);
			statListing.addDatasetGenerator(typeAxis);

			statListing.addFilter(dateFilter);

			addDisplayListing(division, statListing, T_head_statistics + statisticsType, true);
		} catch (Exception e) {
			log.error("Error occurred while creating statistics Paises con mas visualizaciones", e);
		}

		// Ciudades con mas visualizaciones
		try {
			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits());

			statListing.setTitle(T_head_cities + statisticsType);
			statListing.setId("list3");

			DatasetTypeGenerator typeAxis = new DatasetTypeGenerator();
			typeAxis.setType("city");
			typeAxis.setMax(10);
			typeAxis.setIncludeTotal(true);
			typeAxis.setSearchDsoType(selectedObjectFilter);
			statListing.addDatasetGenerator(typeAxis);

			statListing.addFilter(dateFilter);

			addDisplayListing(division, statListing, T_head_statistics + statisticsType, true);
		} catch (Exception e) {
			log.error("Error occurred while creating statistics for Ciudades con mas visualizaciones", e);
		}

		// List of the top 10 items for the entire repository
		try {

			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits());

			statListing.setTitle(T_head_top_views_home);
			statListing.setId("list2");

			// Adding a new generator for our top 10 items without a name length delimiter
			DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
			dsoAxis.addDsoChild(Constants.ITEM, 10, false, -1);
			statListing.addDatasetGenerator(dsoAxis);
			statListing.addFilter(dateFilter);

			// Render the list as a table
			addDisplayListing(division, statListing, T_head_visits_views, false);

		} catch (Exception e) {
			log.error("Error occurred while creating visit statistics for home page", e);
		}

		// 10 items mas descargados
		try {
			StatisticsListing statsList = new StatisticsListing(new StatisticsDataVisits());
			statsList.setTitle(T_head_top_downloads_home);
			statsList.setId("list-bit2");

			DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
			dsoAxis.addDsoChild(Constants.BITSTREAM, 10, false, -1);
			statsList.addDatasetGenerator(dsoAxis);
			statsList.addFilter(dateFilter);
			addDisplayListing(division, statsList, T_head_downloads_bitstream, false);
		} catch (Exception e) {
			log.error("Error occurred while creating download statistics for home page", e);
		}

	}

    public void renderViewer(Division division, DSpaceObject dso, String selectedTimeFilter, int selectedObjectFilter, String statisticsType) throws WingException {

		division.setHead(T_head_dso.parameterize(dso.getName()));

		// Retrieve the optional time filter
		StatisticsSolrDateFilter dateFilter = getDateFilter(selectedTimeFilter);

		// Visitas por mes o por año
		try {

			StatisticsTable statisticsTable = new StatisticsTable(new StatisticsDataVisits(dso));

			statisticsTable.setTitle(T_head_statistics + statisticsType);
			statisticsTable.setId("tab1");

			DatasetTimeGenerator timeAxis = new DatasetTimeGenerator();
			timeAxis.setDateInterval(obtenerTipo(selectedTimeFilter), obtenerInicio(selectedTimeFilter), "+1");
			timeAxis.setIncludeTotal(true);
			statisticsTable.addDatasetGenerator(timeAxis);

			DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
			dsoAxis.addDsoChild(selectedObjectFilter, -1, false, -1);
			statisticsTable.addDatasetGenerator(dsoAxis);

			addDisplayTableExtended(division, statisticsTable, T_head_visits_views);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error occurred while creating statistics for dso with ID: " + dso.getID() + " and type "
					+ dso.getType() + " and handle: " + dso.getHandle(), e);
		}

		// Paises con mas visualizaciones
		try {
			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits(dso));

			statListing.setTitle(T_head_countries + statisticsType);
			statListing.setId("list-country");

			DatasetTypeGenerator typeAxis = new DatasetTypeGenerator();
			typeAxis.setType("countryCode");
			typeAxis.setSearchDsoType(selectedObjectFilter);
			typeAxis.setMax(10);
			typeAxis.setIncludeTotal(true);
			statListing.addDatasetGenerator(typeAxis);

			statListing.addFilter(dateFilter);

			addDisplayListing(division, statListing, T_head_statistics + statisticsType, true);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error occurred while creating statistics for dso with ID: " + dso.getID() + " and type "
					+ dso.getType() + " and handle: " + dso.getHandle(), e);
		}

		// Ciudades con mas visualizaciones
		try {
			StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits(dso));

			statListing.setTitle(T_head_cities + statisticsType);
			statListing.setId("list3");

			DatasetTypeGenerator typeAxis = new DatasetTypeGenerator();
			typeAxis.setType("city");
			typeAxis.setSearchDsoType(selectedObjectFilter);
			typeAxis.setMax(10);
			typeAxis.setIncludeTotal(true);
			statListing.addDatasetGenerator(typeAxis);

			statListing.addFilter(dateFilter);

			addDisplayListing(division, statListing, T_head_statistics + statisticsType, true);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error occurred while creating statistics for dso with ID: " + dso.getID() + " and type "
					+ dso.getType() + " and handle: " + dso.getHandle(), e);
		}

		// List of the top 10 items for the community/colection
		if (!(dso instanceof org.dspace.content.Item)) {
			try {

				StatisticsListing statListing = new StatisticsListing(new StatisticsDataVisits(dso));

				statListing.setTitle(T_head_top_views_home);
				statListing.setId("list2");

				// Adding a new generator for our top "n" items without a name length delimiter
				DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
				dsoAxis.addDsoChild(Constants.ITEM, 10, false, -1);
				statListing.addDatasetGenerator(dsoAxis);
				statListing.addFilter(dateFilter);

				// Render the list as a table
				addDisplayListing(division, statListing, T_head_visits_views, false);

			} catch (Exception e) {
				log.error("Error occurred while creating visit statistics for home page", e);
			}
		}

		if (dso instanceof org.dspace.content.Item) {
			try {
				StatisticsListing statsList = new StatisticsListing(new StatisticsDataVisits(dso));

				statsList.setTitle(T_head_temporal_bitstream);
				statsList.setId("list-bit-temporal");

				DatasetDSpaceObjectGenerator dsoAxis = new DatasetDSpaceObjectGenerator();
				dsoAxis.addDsoChild(Constants.BITSTREAM, 10, false, -1);
				statsList.addDatasetGenerator(dsoAxis);

				statsList.addFilter(dateFilter);

				addDisplayListing(division, statsList, T_head_downloads_bitstream, false);

			} catch (Exception e) {
				log.error("Error occurred while creating download statistics for home page", e);
			}
		}

    }


    /**
     * Se modifica para acumular todos los valores de "matrix" al primero para las estadisticas generales.
     * Adds a table layout to the page
     * 
     * @param mainDiv
     *            the div to add the table to
     * @param display
     * @throws SAXException
     * @throws WingException
     * @throws ParseException
     * @throws IOException
     * @throws SolrServerException
     * @throws SQLException
     */
    private void addDisplayTableExtended(Division mainDiv, StatisticsTable display,String message)
	    throws SAXException, WingException, SQLException,
	    SolrServerException, IOException, ParseException {

	String title = display.getTitle();

	Dataset dataset = display.getDataset();

	if (dataset == null) {
		/** activate dataset query */
		dataset = display.getDataset(context);
	}

	if (dataset != null && dataset.getMatrix().length>0) {

		String[][] matrix = acumula(dataset.getMatrix());

		/** Generate Table */
		Division wrapper = mainDiv.addDivision("tablewrapper");
		Table table = wrapper.addTable("list-table", 1, 1,
				title == null ? "detailtable" : "tableWithTitle detailtable");
		if (title != null)
                    {
                        table.setHead(message(title));
                    }

		/** Generate Header Row */
		Row headerRow = table.addRow();
		headerRow.addCell("spacer", Cell.ROLE_HEADER, "labelcell").addContent(dataset.getRowLabels().get(0));;

		String[] cLabels = dataset.getColLabels().toArray(new String[0]);
		for (int row = 0; row < cLabels.length; row++) {
			Cell cell = headerRow.addCell(0 + "-" + row + "-h",
                Cell.ROLE_HEADER, "labelcell");
			cell.addContent(cLabels[row]);
		}

		/** Generate Table Body */
		for (int row = 0; row < matrix.length; row++) {
			Row valListRow = table.addRow();

			/** Add Row Title */
			valListRow.addCell("" + row, Cell.ROLE_DATA, "labelcell")
					.addContent(message(message));

			/** Add Rest of Row */
			for (int col = 0; col < matrix[row].length; col++) {
				Cell cell = valListRow.addCell(row + "-" + col,
						Cell.ROLE_DATA, "datacell");
				cell.addContent(matrix[row][col]);
			}
		}
	}

    }

    private String[][] acumula(String[][] matrix) {
	String[][] matrixAcumulada=new String[1][matrix[0].length];
	for(int k=0;k<matrixAcumulada[0].length;k++){
	    matrixAcumulada[0][k]="0";
	}
	for(int i=0;i<matrix.length;i++){
	    for(int j=0;j<matrix[0].length;j++){
		matrixAcumulada[0][j]=String.valueOf(Integer.parseInt(matrixAcumulada[0][j])+Integer.parseInt(matrix[i][j]));
	    }
	}
	
	    
	return matrixAcumulada;
    }

    private void addDisplayListing(Division mainDiv, StatisticsListing display,String message, boolean setOthers)
	    throws SAXException, WingException, SQLException,
	    SolrServerException, IOException, ParseException {

	String title = display.getTitle();

	Dataset dataset = display.getDataset();

	if (dataset == null) {
	    /** activate dataset query */
	    dataset = display.getDataset(context);
	}

	if (dataset != null && dataset.getMatrix().length>0) {

	    String[][] matrix = dataset.getMatrix();
	    java.util.List<String> labels =dataset.getColLabels();
	    java.util.List<Map<String,String>> labelsAttrs =dataset.getColLabelsAttrs();
	    if (setOthers) {
		    setOthers(matrix[0],labels);
	    }
	    ordena(matrix,labels,labelsAttrs);

	    // String[] rLabels = dataset.getRowLabels().toArray(new String[0]);

	    Table table = mainDiv.addTable(display.getId(), matrix.length, 2,
		    title == null ? "detailtable" : "tableWithTitle detailtable");
	    if (title != null)
	    {
		table.setHead(message(title));
	    }

	    Row headerRow = table.addRow();

	    headerRow.addCell("", Cell.ROLE_HEADER, "labelcell");

	    headerRow.addCell("", Cell.ROLE_HEADER, "labelcell").addContent(message(message));

	    /** Generate Table Body */
	    for (int col = 0; col < matrix[0].length; col++) {
		//Adan: si el valor es 0 no nos interesa
		if(!"0".equalsIgnoreCase(matrix[0][col])){
        		Row valListRow = table.addRow();
        
        		Cell catCell = valListRow.addCell(col + "1", Cell.ROLE_DATA, "labelcell");
        		if(labelsAttrs.get(col).containsKey("url")){
        		    catCell.addXref(labelsAttrs.get(col).get("url"),labels.get(col));
        		}else{
        		    catCell.addContent(labels.get(col));
        		}
        		Cell valCell = valListRow.addCell(col + "2", Cell.ROLE_DATA, "datacell");
        		valCell.addContent(matrix[0][col]); 
		}
	    }

	    if (!"".equals(display.getCss())) {
		List attrlist = mainDiv.addList("divattrs");
		attrlist.addItem("style", display.getCss());
	    }

	}

    }

    private void ordena(String[][] matrix, java.util.List<String> labels, java.util.List<Map<String, String>> labelsAttrs) {
	for(int i = 0; i < matrix[0].length; i++) {
	    for(int j = 0; j < matrix[0].length; j++) {
		if(Integer.parseInt(matrix[0][i]) > Integer.parseInt(matrix[0][j])) {
		    String aux1 = matrix[0][i];
		    matrix[0][i] = matrix[0][j];
		    matrix[0][j] = aux1;
		    
		    aux1=labels.get(i);
		    labels.set(i, labels.get(j));
		    labels.set(j,aux1);
		    
		    Map<String, String> aux2;
		    aux2=labelsAttrs.get(i);
		    labelsAttrs.set(i, labelsAttrs.get(j));
		    labelsAttrs.set(j,aux2);
		}
	    }
	}
    }

    private void setOthers(String[] values, java.util.List<String> labels) {
        int total = 0;
	    for(int i = 0; i < values.length-1; i++) {
	        if (labels.get(i).equals("--")) {
	            labels.set(i, "Unknown");
	        }
	        total += Integer.parseInt(values[i]);
	    }
	    int others = Integer.parseInt(values[values.length-1]) - total;
	    values[values.length-1] = String.valueOf(others);
	    labels.set(labels.size()-1, "Others");
    }

    protected StatisticsSolrDateFilter getDateFilter(String timeFilter){
	if(StringUtils.isNotEmpty(timeFilter))
	{
	    StatisticsSolrDateFilter dateFilter = new StatisticsSolrDateFilter();
	    dateFilter.setStartStr(obtenerInicio(timeFilter));
	    dateFilter.setEndStr("+1");
	    dateFilter.setTypeStr(obtenerTipo(timeFilter));
	    return dateFilter;
	}else{
	    // Por defecto 12 meses
	    StatisticsSolrDateFilter dateFilter = new StatisticsSolrDateFilter();
	    dateFilter.setStartStr("-12");
	    dateFilter.setEndStr("+1");
	    dateFilter.setTypeStr("month");
	    return dateFilter;
	}
    }

    //El tipo es mes por defecto
    private String obtenerTipo(String timeFilter) {
	if(timeFilter!=null){
	    char tipo=timeFilter.charAt(timeFilter.length()-1);
	    switch (tipo){
	    	case 'm':return "month";
	    	case 'y':return "year";
	    	case 'd':return "day";
	    	default:return "month";
	    }
	}
	return "month";
    }

    private String obtenerInicio(String timeFilter) {
		if (timeFilter != null) {
			String inicio = timeFilter.substring(0, timeFilter.length() - 1);
			try {
				int inicioNum = Integer.parseInt(inicio);
				// Force a maximum of 60 months or years in case someone changed the value
				if (inicioNum < -60 || inicioNum >= 0) {
					// Someone changed the value of the time filter, return a default of -12
					inicio = "-12";
				}
			} catch (NumberFormatException e) {
				// Value of the filter is not a number, return default of -12
				inicio = "-12";
			}
			return inicio;
		}
		return timeFilter;
    }

    protected void addTimeFilter(Division mainDivision) throws WingException {
	Request request = ObjectModelHelper.getRequest(objectModel);
	String selectedTimeFilter = request.getParameter("time_filter");

	Para para=mainDivision.addPara("time_filter_para","time_filter");
	para.addContent(message(T_time_filter_seleccione));
	Radio timeFilter = para.addRadio("time_filter");
	timeFilter.addOption(StringUtils.isBlank(selectedTimeFilter) || StringUtils.equals(selectedTimeFilter, "-12m"), "-12m", T_time_filter_month);
	timeFilter.addOption(StringUtils.equals(selectedTimeFilter,"-10y"), "-10y", T_time_filter_year);
    }

    protected void addObjectFilter(Division mainDivision, DSpaceObject dso) throws WingException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String selectedNumItemsFilter = request.getParameter("object_filter");

        Para para=mainDivision.addPara("object_filter_para","object_filter");
        para.addContent(message(T_statistics_type_filter_title));
        Radio objectFilter = para.addRadio("object_filter");
        objectFilter.addOption(StringUtils.equals(selectedNumItemsFilter, "0"), "0", T_statistics_type_filter_download);
        if (dso != null && dso instanceof org.dspace.content.Item) {
            objectFilter.addOption(StringUtils.equals(selectedNumItemsFilter, "1"), "1", T_statistics_type_filter_item_view);
        }
        else {
            objectFilter.addOption(StringUtils.equals(selectedNumItemsFilter, "1"), "1", T_statistics_type_filter_items_views);
        }
        objectFilter.addOption(StringUtils.isBlank(selectedNumItemsFilter) || StringUtils.equals(selectedNumItemsFilter, "2"), "2", T_statistics_type_filter_any_view);
    }

    private void addButton(Division division) throws WingException {
        Para para=division.addPara();
        Button searchButton = para.addButton("search-button");
        searchButton.setValue(T_general_search);
    }
    
    @Override
    protected Message getNoResultsMessage() {
	return T_no_results;
    }

}
