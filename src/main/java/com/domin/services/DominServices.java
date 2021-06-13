package com.domin.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.domin.Constants;
import com.domin.pojo.BarChart;
import com.domin.pojo.ChinaBorderShare;

@Service
public class DominServices {

	@Autowired(required = true)
	@Qualifier(value = "countriesData")
	public JSONArray countriesData;

	@Autowired(required = true)
	public PdfService pdfService;

	@Value("${generated.pdf.location}")
	String generatedPdfLocation;

	/**
	 * Returns the Set of Top 10 countries with most borders, if 2 or more countries
	 * have same border count, they are oder lexicographically
	 * 
	 * @return
	 */
	public List<BarChart> getTopMostBorders(Integer limit) {
		Set<BarChart> alpha3Code = new TreeSet<BarChart>(Comparator.comparing(BarChart::getBorderCount)
				.thenComparing(BarChart::getCountryName).thenComparing(BarChart::getAlpha3Code).reversed());

		countriesData.forEach(obj -> {
			JSONObject object = (JSONObject) obj;
			alpha3Code.add(new BarChart(object.getString("name"), object.getString("alpha3Code"),
					object.getJSONArray("borders").length()));
		});

		return alpha3Code.stream().limit(limit == null ? Constants.LIMIT : limit).collect(Collectors.toList());
	}

	/**
	 * Returns the list of all countries which shares border with China
	 */
	public List<ChinaBorderShare> getChinaBorderCountries() {

		List<ChinaBorderShare> chinaBorderList = new ArrayList<ChinaBorderShare>();

		countriesData.forEach(obj -> {
			JSONObject object = (JSONObject) obj;
			if (!object.getString("alpha3Code").equals(Constants.CHINA_ALPHA_CODE)) {
				JSONArray arr = object.getJSONArray("borders");

				if (arr.toList().contains(Constants.CHINA_ALPHA_CODE))
					chinaBorderList.add(new ChinaBorderShare(object.getString("name"), object.getString("region"),
							object.getDouble("area"), object.getLong("population"), object.getString("flag")));
			}
		});

		return chinaBorderList;
	}

	/**
	 * Generates a PDF for the dashboard
	 * 
	 * @throws IOException
	 */
	public Resource generatePdfForDashboard(Integer limit) throws IOException {

		File file = new File(this.generatedPdfLocation);
		this.pdfService.generatePdf(Arrays.asList("Name", "Region", "Area", "Population", "Flag"),
				this.getTopMostBorders(limit == null ? 10 : limit), this.getChinaBorderCountries());
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return resource;
	}

}
