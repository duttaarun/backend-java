package com.domin.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.jfree.chart.ChartUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.domin.Constants;
import com.domin.pojo.BarChart;
import com.domin.pojo.ChinaBorderShare;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.svg.converter.SvgConverter;

@Service
public class PdfService {

	@Autowired
	ChartServices chartService;

	@Value("${generated.pdf.location}")
	String generatedPdfLocation;

	@Value("${generated.chart.location}")
	String generatedChartLocation;

	@Value("${generated.svg.location}")
	String generatedSvgLocation;

	public void generatePdf(List<String> columnList, List<BarChart> chartData,
			List<ChinaBorderShare> chinaBorderCountries) throws IOException {
		PdfWriter writer = new PdfWriter(new FileOutputStream(new File(this.generatedPdfLocation)),
				new WriterProperties().setCompressionLevel(0));
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		
		//Add a header
		document.add(new Paragraph(
				"Demo Application").setFontColor(ColorConstants.BLACK).setFont(font).setBold()
				.setTextAlignment(TextAlignment.CENTER));
		
		//Add some spaces
		spaceAdder(document, 3);

		//Add the barchart
		this.setDashboardBarchartInPdf(chartData, document);

		//Add some spaces
		spaceAdder(document, 2);
		
		//Add the table of data
		document.add(new Paragraph("Countries Bordered By China").setFontColor(ColorConstants.BLACK).setFont(font)
				.setBold().setTextAlignment(TextAlignment.CENTER));

		Table table = new Table(UnitValue.createPercentArray(columnList.size())).useAllAvailableWidth();
		this.addTableHeader(table, columnList);
		this.addTableRows(table, pdf, chinaBorderCountries);

		document.add(table);

		// Close document
		document.close();
		pdf.close();
	}
	
	private void addTableHeader(Table table, List<String> columnList) throws IOException {

		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		columnList.stream().forEach(columnTitle -> {
			Cell cell = new Cell().add(new Paragraph(columnTitle).setFont(font).setFontColor(ColorConstants.WHITE));
			cell.setBackgroundColor(ColorConstants.GREEN);
			cell.setTextAlignment(TextAlignment.CENTER);
			table.addCell(cell);
		});
	}

	private void addTableRows(Table table, PdfDocument doc, List<ChinaBorderShare> chinaBorderCountries)
			throws FileNotFoundException, IOException {
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		chinaBorderCountries.stream().forEach(country -> {

			Cell cell = new Cell().add(new Paragraph(country.getCountryName()).setFont(font));
			cell.setTextAlignment(TextAlignment.CENTER);
			table.addCell(cell);

			Cell cell1 = new Cell().add(new Paragraph(country.getRegion()).setFont(font));
			cell1.setTextAlignment(TextAlignment.CENTER);
			table.addCell(cell1);

			Cell cell2 = new Cell().add(new Paragraph(String.valueOf(country.getArea())).setFont(font));
			cell2.setTextAlignment(TextAlignment.CENTER);
			table.addCell(cell2);

			Cell cell3 = new Cell().add(new Paragraph(String.valueOf(country.getPopulation())).setFont(font));
			cell3.setTextAlignment(TextAlignment.CENTER);
			table.addCell(cell3);

			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				File file = new File(this.generatedSvgLocation
						+ DatatypeConverter.printHexBinary(md.digest(country.getFlagSvgUrl().getBytes())) + ".svg");
				if (!file.exists()) {
					RestTemplate restClient = new RestTemplate(
							new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
					byte[] imageBytes = restClient.getForObject(country.getFlagSvgUrl(), byte[].class);
					Files.write(Paths.get(this.generatedSvgLocation
							+ DatatypeConverter.printHexBinary(md.digest(country.getFlagSvgUrl().getBytes())) + ".svg"),
							imageBytes);
				}

				Image img = SvgConverter.convertToImage(new FileInputStream(new File(this.generatedSvgLocation
						+ DatatypeConverter.printHexBinary(md.digest(country.getFlagSvgUrl().getBytes())) + ".svg")),
						doc);
				img.setWidth(30).setHeight(15);
				table.addCell(img.setHorizontalAlignment(HorizontalAlignment.CENTER));
			} catch (Exception e) {
				e.printStackTrace();
				Cell imageAlt = new Cell().add(new Paragraph(Constants.INVALID_IMAGE_MSG).setFont(font));
				imageAlt.setTextAlignment(TextAlignment.CENTER);
				table.addCell(imageAlt);
			}
		});
	}

	/**
	 * Function to add the barchart to the PDF
	 * @param chartData
	 * @param document
	 */
	private void setDashboardBarchartInPdf(List<BarChart> chartData, Document document) {
		try {
			ChartUtilities.writeChartAsPNG(new FileOutputStream(this.generatedChartLocation),
					this.chartService.generateBarChart(chartData), Constants.CHART_WIDTH, Constants.CHART_HEIGHT);
			Image img = new Image(ImageDataFactory.create(this.generatedChartLocation));
			document.add(img.setHorizontalAlignment(HorizontalAlignment.CENTER).setAutoScale(true));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Add repeated spaces in PDF
	 * @param document
	 * @param lim
	 */
	private void spaceAdder(Document document, Integer lim) {
		for(int i=0;i<lim;i++) {
		document.add(new Paragraph(""));
		}
	}

}
