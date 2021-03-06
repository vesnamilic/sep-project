package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import sep.project.model.Magazine;
import sep.project.repository.MagazineRepository;
import sep.project.repository.ScientificAreaRepository;

@Service
public class MagazineService {

	@Autowired
	private MagazineRepository magazineRepository;

	@Autowired
	private ScientificAreaRepository scientificAreaRepository;

	public ResponseEntity<List<Magazine>> getMagazines(){
		List<Magazine>magazines=magazineRepository.findAll();
		return new ResponseEntity<>(magazines, HttpStatus.OK);
	}
	
	public Magazine findByEmail(String email) 
	{
		return this.magazineRepository.findByEmail(email);
	}
	

}
