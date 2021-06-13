package com.domin.services;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import com.domin.pojo.BarChart;

@Service
public class ChartServices {

	public JFreeChart generateBarChart(List<BarChart> details) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

		details.stream().forEach(data -> {
			dataSet.setValue(data.getBorderCount(), "DATA", data.getCountryName());
		});

		JFreeChart chart = ChartFactory.createBarChart("Most No. of borders(Top 10)", "Countries", "Number of Borders",
				dataSet, PlotOrientation.VERTICAL, false, true, false);

		chart.getPlot().setBackgroundPaint(Color.WHITE);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.getRenderer().setSeriesPaint(0, new Color(0, 128, 0));

		return chart;
	}
}
