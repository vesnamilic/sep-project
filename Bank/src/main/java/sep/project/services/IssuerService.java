package sep.project.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.enums.Status;
import sep.project.model.Card;
import sep.project.model.CardOwner;
import sep.project.model.Transaction;
import sep.project.repository.CardOwnerRepository;
import sep.project.repository.CardRepository;
import sep.project.repository.TransactionRepository;

@Service
public class IssuerService {

	@Autowired
	CardOwnerRepository cardOwnerRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	CardRepository cardRepository;
	
	private static String replyToPCC;

	@Value("${replyToPCC}")
    public void setsss(String s) {
        replyToPCC = s;
    }
	
	public void checkPayment(PCCRequestDTO request) {

		CardOwner cardOwnrt = cardOwnerRepository.findByCardPan(request.getSenderPan());
		Transaction t = createTransakcija(request, cardOwnrt);

		if (cardOwnrt != null) {
			if (checkCredentials(request, cardOwnrt)) {
				processPayment(request, t, cardOwnrt);
			} else {
				System.out.println("Buyer credentials is not valid!");
				save(t, Status.N);
				PCCResponseDTO pccResponseDTO = new PCCResponseDTO();
				pccResponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
				pccResponseDTO.setStatus(Status.N);
				sendReply(pccResponseDTO, t);
			}
		} else {
			System.out.println("Buyer does not have account in this bank!");
			save(t, Status.N);
			PCCResponseDTO pccResponseDTO = new PCCResponseDTO();
			pccResponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
			pccResponseDTO.setMerchantOrderID(request.getMerchantOrderID());
			pccResponseDTO.setStatus(Status.N);
			sendReply(pccResponseDTO, t);
		}
	}

	public boolean checkCredentials(PCCRequestDTO request, CardOwner k) {

		if (k == null) {
			return false;
		}

		if (!k.getLastName().toLowerCase().equals(request.getName().toLowerCase().trim())) {
			return false;
		}

		if (!k.getLastName().toLowerCase().equals(request.getLastName().toLowerCase().trim())) {
			return false;
		}

		if (!k.getCard().getCvv().equals(request.getCvv().trim())) {
			return false;
		}

		String expDate = request.getMonth() + "/" + request.getYear();
		if (!k.getCard().getExpDate().equals(expDate)) {
			return false;
		}

		return true;

	}

	public Transaction createTransakcija(PCCRequestDTO request, CardOwner cardOwner) {

		Card card = cardRepository.findByPan(request.getSenderPan());

		Transaction t = new Transaction();
		t.setBuyer(cardOwner);
		t.setSeller(null);
		t.setPaymentURL("");
		t.setTimestamp(new Date());
		t.setStatus(Status.K);
		t.setBuyerPan(request.getRecieverPan());
		if (card != null)
			t.setBuyerPan(card.getPan());
		t.setAmount(request.getAmount());
		t.setErrorURL("");
		t.setFailedURL("");
		t.setSuccessURL("");
		t.setMerchantOrderId(request.getMerchantOrderID());
		t.setMerchantTimestamp(request.getMerchantTimestamp());
		transactionRepository.save(t);
		return t;

	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void save(Transaction t, Status s) {
		t.setStatus(s);
		transactionRepository.save(t);
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void processPayment(PCCRequestDTO request, Transaction t, CardOwner cardOwner){

        PCCResponseDTO pccResponseDTO = new PCCResponseDTO();
        pccResponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
        pccResponseDTO.setMerchantOrderID(request.getMerchantOrderID());
        Card card = cardRepository.findByPan(t.getBuyerPan());
       
        if (card==null){
            System.out.println("Transakcija neuspesna, nije pronadjena kartica.");
            t.setStatus(Status.N);
            transactionRepository.save(t);
            pccResponseDTO.setStatus(Status.N);
            sendReply(pccResponseDTO, t);
            return;
        }

        Float available = card.getAvailableFunds();

        if(available - t.getAmount() < 0){
            System.out.println("Transakcija neuspesna, nedovoljno sredstava.");
            t.setStatus(Status.N);
            transactionRepository.save(t);
            pccResponseDTO.setStatus(Status.N);
            sendReply(pccResponseDTO, t);
            return;
        }

        card.setAvailableFunds(available - t.getAmount());
        cardRepository.save(card);

        cardOwner.setCard(card);
        cardOwnerRepository.save(cardOwner);

        t.setStatus(Status.U);
        transactionRepository.save(t);

        pccResponseDTO.setIssuerOrderID(t.getId());
        pccResponseDTO.setIssuerTimestamp(t.getTimestamp());
        pccResponseDTO.setStatus(Status.U);
        System.out.println("Transakcija je uspesna.");
        sendReply(pccResponseDTO, t);

    }
	
    public void sendReply(PCCResponseDTO response, Transaction t) {

        RestTemplate template = new RestTemplate();
        try {
            template.postForEntity(replyToPCC, response, PCCResponseDTO.class);
        }catch (Exception e){

            if(t.getStatus()==Status.N)
                save(t, Status.N_PCC);
            else save(t, Status.U_PCC);
        }
    }

}
