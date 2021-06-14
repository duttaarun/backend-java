package com.domin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.domin.pojo.BarChart;
import com.domin.pojo.ChinaBorderShare;
import com.domin.services.DominServices;

@CrossOrigin(origins = { "http://localhost", "http://domin-app.eastus.cloudapp.azure.com" }, maxAge = 3600)
@RestController
public class DominController {

	@Autowired(required = true)
	private DominServices dominService;

	@GetMapping(path = "/getTopMostBorders", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BarChart>> getTopMostBorders(Integer limit) {
		return ResponseEntity.ok().body(dominService.getTopMostBorders(limit));
	}

	@GetMapping(path = "/getChinaBorderList", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ChinaBorderShare>> getChinaBorderList() {
		return ResponseEntity.ok().body(dominService.getChinaBorderCountries());
	}

	@GetMapping(path = "/generatePdfForDashboard")
	public ResponseEntity<Resource> generatePdfForDashboard(Integer limit) throws IOException {

		Resource resource = this.dominService.generatePdfForDashboard(limit);

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=data.pdf");
		return ResponseEntity.ok().headers(header).contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);
	}
}
