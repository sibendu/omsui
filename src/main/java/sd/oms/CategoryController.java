package sd.oms;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sd.oms.model.Customer;
import sd.oms.model.CustomerRepository;
import sd.oms.model.ProductCategory;
import sd.oms.model.ProductCategoryRepository;
import sd.oms.model.SKUItem;
import sd.oms.service.CatalogService;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/catalog")
public class CategoryController {

	@Autowired
	private CatalogService catalogService;

	@RequestMapping(value = "/{seller}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> findCatalog(@PathVariable(name = "seller", required = true) String seller)
			throws Exception {

		ResponseEntity<String> response;

		ProductCategory root = catalogService.findCatalog(seller);

		String productJson = root.toJSON();

		response = new ResponseEntity<String>(productJson, HttpStatus.OK);
		return response;
	}

	@GetMapping(path = "/seed") //
	public @ResponseBody String seedCatalog() throws Exception{
		// ProductCategory root = Test.getDummyCatalog();

		String seller = "2222";

		ProductCategory cat = null;
		SKUItem item = null;

		ProductCategory rootCatalog = new ProductCategory(null, seller, "Root Catalog", "Root Catalog", null, null);
		catalogService.save(rootCatalog);
		System.out.println("Saved root catalog: " + rootCatalog.getId());

		ProductCategory cat1 = new ProductCategory(null, seller, "Super Category 10", "Super Category 10",
				rootCatalog.getId(), null);
		ProductCategory cat2 = new ProductCategory(null, seller, "Super Category 20", "Super Category 20",
				rootCatalog.getId(), null);
		ProductCategory cat3 = new ProductCategory(null, seller, "Super Category 30", "Super Category 30",
				rootCatalog.getId(), null);
		ProductCategory cat4 = new ProductCategory(null, seller, "Super Category 40", "Super Category 40",
				rootCatalog.getId(), null);

		catalogService.save(cat1);
		catalogService.save(cat2);
		catalogService.save(cat3);
		catalogService.save(cat4);
		System.out.println("Saved level 1 categories");

		Long parentId = null;

		for (int i = 0; i < 30; i++) {

			if (i < 5) {
				parentId = cat1.getId();
			} else if (i >= 5 && i < 12) {
				parentId = cat2.getId();
			} else if (i >= 12 && i < 20) {
				parentId = cat3.getId();
			} else {
				parentId = cat4.getId();
			}

			int catId = (i + 1) * 100;

			cat = new ProductCategory(null, seller, "Category-" + catId, "Category Description-" + catId, parentId,
					null);

			for (int k = 1; k < 25; k++) {

				long val = catId + 10 + k;

				item = new SKUItem(null, "sku-" + val, "Item -" + val, "Item Description :::: " + val, new Double(val),
						"kg", new Double(1), new Double(5), new Double(0.5), new Double(2), cat);

				cat.addItem(item);
			}

			catalogService.save(cat);
			System.out.println("Saved category " + cat.getId() + "for parent = " + parentId);

		}

		return "Catalog seeded successfully";
	}

	@GetMapping(path = "/clean") //
	public @ResponseBody String cleanCatalog() throws Exception{
		catalogService.deleteAll();
		return "All catalog deleted";
	}

}