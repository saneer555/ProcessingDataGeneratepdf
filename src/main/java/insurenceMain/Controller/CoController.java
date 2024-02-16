package insurenceMain.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import insurenceMain.Binding.CoResponse;
import insurenceMain.Services.CoServices;

@RestController
public class CoController {
	
	@Autowired
	private CoServices coServices;
	
	
	@GetMapping("/process")
	public CoResponse getProccesingData() {
		return coServices.processPendingTriggers();
	}

}
