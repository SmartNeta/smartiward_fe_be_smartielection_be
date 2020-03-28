package com.mnt.sampark.core.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.opencsv.CSVWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class CSVServiceGenerator {
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	 
	 
	 @Autowired 
	 Configuration freemarkerConfig;
	 
	 public String getQueryFor(String classType) throws SQLException, IOException {
		 if(classType.startsWith("Mst")) {
			 String sql = jdbcTemplate.queryForObject("Select query from core_report_metadata where type = 'grid' and class_type = '" + classType + "'", String.class);
			 return sql;
		 } else {
			 return null;
		 }
	 }
	 
	public String generateCSVAsString(String sql) throws SQLException, IOException {
		Writer writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer);
		jdbcTemplate.query(sql, new ResultSetExtractor<Object>(){
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {
					csvWriter.writeAll(rs, true);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						csvWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});
		return writer.toString();
	}
	public static final String CSS = "tr { text-align: center; } th { background-color: lightgreen; padding: 3px; } td {background-color: lightblue;  padding: 3px; }";
	 
	public void generatePDF(String sql, OutputStream os) throws SQLException, IOException, DocumentException {
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> headers =  new ArrayList<String>();
		
		jdbcTemplate.query(sql, new ResultSetExtractor<Object>(){
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					 int count = rs.getMetaData().getColumnCount();
					 for(int i = 0; i<count; i++) {
						 headers.add(rs.getMetaData().getColumnLabel(i + 1));
					 }
					 
					 while (rs.next()) {
						 List<String> row = new ArrayList<String>();
						 for(int i = 0; i<count; i++) {
							 Object o = rs.getObject(i + 1);
							 if(o != null) {
								 row.add(o.toString());
							 } else {
								 row.add("");
							 }
						 }
						 rows.add(row);
					 }
				
				return null;
			}
		});
		Template template = freemarkerConfig.getTemplate("pdf-grid.ftl");
		Map<String, Object> input = new HashMap<String, Object>();
		input.put("rows", rows);
		input.put("headers", headers);
		Writer stringWriter = new StringWriter();
        try {
			template.process(input, stringWriter);
		} catch (TemplateException e) {
			e.printStackTrace();
		}
        
        Document document = new Document();
        
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        
        CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);
 
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
 
        PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
 
        XMLWorker worker = new XMLWorker(css, true);
        XMLParser p = new XMLParser(worker);
        p.parse(new ByteArrayInputStream(stringWriter.toString().getBytes()));
 
        document.close();
        writer.close();
		
	}

}
