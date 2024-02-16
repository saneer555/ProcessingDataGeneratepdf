package insurenceMain.Repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import insurenceMain.Entity.CoTriggres;

public interface CoTriggersRepository extends JpaRepository<CoTriggres, Serializable>{
	
	public List<CoTriggres> findByCoStatus(String coStatus);
	
	public CoTriggres findByCaseNum(Integer caseNum);

}
