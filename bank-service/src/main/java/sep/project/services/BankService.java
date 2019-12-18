package sep.project.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.BankResponseDTO;

@Service
public class BankService {

	public ResponseEntity<BankResponseDTO> initiatePayment(BankRequestDTO requestDTO) {

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<BankResponseDTO> responseDTO = template
					.postForEntity("https://localhost:8081/api/firstRequest", requestDTO, BankResponseDTO.class);
			if (responseDTO != null) {
				return responseDTO;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Bank is not available!");
			return null;
		}

	}
	
}