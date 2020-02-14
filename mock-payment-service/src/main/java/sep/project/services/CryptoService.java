package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Crypto;
import sep.project.repositories.CryptoRepository;

@Service
public class CryptoService {

	@Autowired
	private CryptoRepository cryptoRepository;
	
	public CryptoService() {
		
	}
	
	public void save(Crypto c) {
		cryptoRepository.save(c);
	}
	
	public Crypto findByText(String text) {
		return cryptoRepository.findByText(text);
	}
}