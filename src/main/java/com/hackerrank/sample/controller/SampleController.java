package com.hackerrank.sample.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hackerrank.sample.dto.FilteredProducts;
import com.hackerrank.sample.dto.SortedProducts;

@RestController
public class SampleController {


	final String uri = "https://jsonmock.hackerrank.com/api/inventory";
	RestTemplate restTemplate = new RestTemplate();
	String result = restTemplate.getForObject(uri, String.class);
	JSONObject root = new JSONObject(result);

	JSONArray data = root.getJSONArray("data");

	@CrossOrigin
	@GetMapping("/filter/price/{initial_price}/{final_price}")
	private ResponseEntity< ArrayList<FilteredProducts> > filtered_books(
			@PathVariable("initial_price") int init_price,
			@PathVariable("final_price") int final_price) {
		try {
			ArrayList<FilteredProducts> books = new ArrayList<FilteredProducts>();

			for (int i = 0; i < data.length(); i++) {
				JSONObject jsonObject = data.getJSONObject(i);
				if ((int) jsonObject.get("price")>init_price && (int) jsonObject.get("price")<final_price) {
					books.add(new FilteredProducts(jsonObject.get("barcode").toString()));
				}
			}
			return books.isEmpty() ?
					new ResponseEntity<>(books, HttpStatus.BAD_REQUEST) :
					new ResponseEntity<>(books, HttpStatus.OK);

		}catch(Exception E) {
			System.out.println("Error encountered : "+E.getMessage());
			return new ResponseEntity<ArrayList<FilteredProducts>>(HttpStatus.NOT_FOUND);
		}
	}

	@CrossOrigin
	@GetMapping("/sort/price")
	private ResponseEntity<SortedProducts[]> sorted_books() {
		try {
			SortedProducts[] ans=new SortedProducts[data.length()];

			ArrayList<JSONObject> list = new ArrayList<>();
			for (int i = 0; i < data.length(); i++) {
				JSONObject jsonObject = data.getJSONObject(i);
				list.add(jsonObject);
			}
			list.sort(Comparator.comparing(json -> (int) json.get("price")));

			for (int i = 0; i < data.length(); i++) {
				ans[i] = new SortedProducts(list.get(i).get("barcode").toString());
			}
			return new ResponseEntity<SortedProducts[]>(ans, HttpStatus.OK);
		} catch(Exception E) {
			System.out.println("Error encountered : "+E.getMessage());
			return new ResponseEntity<SortedProducts[]>(HttpStatus.NOT_FOUND);
		}
	}

}
