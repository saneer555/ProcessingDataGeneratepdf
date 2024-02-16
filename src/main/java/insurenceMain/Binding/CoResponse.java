package insurenceMain.Binding;

import lombok.Data;

@Data
public class CoResponse {
	
	private Integer totalTriggers;
	
	private Integer succTriggers;
	
	private Integer failedTriggers;

}
