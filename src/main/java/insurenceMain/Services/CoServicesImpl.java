package insurenceMain.Services;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import insurenceMain.Binding.CoResponse;
import insurenceMain.Entity.ApplicantData;
import insurenceMain.Entity.CoTriggres;
import insurenceMain.Entity.EligiblityDetermination;
import insurenceMain.Entity.UserApp;
import insurenceMain.MailUtils.EmailUtls;
import insurenceMain.Repository.ApplicantDataRepository;
import insurenceMain.Repository.CoTriggersRepository;
import insurenceMain.Repository.EligibleDeterminationRepository;
import insurenceMain.Repository.UserAppRespository;

@Service
public class CoServicesImpl implements CoServices{


	@Autowired
	private EligibleDeterminationRepository elgRepo;
	
	@Autowired
	private CoTriggersRepository coRepo;
	
	@Autowired
	private ApplicantDataRepository userRepo;
	
	@Autowired
	private UserAppRespository appRepo;

	@Autowired
	private EmailUtls emailSend;
	
	
	@Override
	public CoResponse processPendingTriggers() {

		CoResponse response = new CoResponse();

		UserApp userApp = null;

		// fletch all pending triggers
		List<CoTriggres> planStatus = coRepo.findByCoStatus("Pending");


		response.setTotalTriggers(planStatus.size());


		//process each pending triggers


		for(CoTriggres pending : planStatus) {


			//get elgble data based on case num
			EligiblityDetermination caseNum = elgRepo.findByCaseNum(pending.getCaseNum());

			//get citizen data based on case num

			Optional<ApplicantData> byId = userRepo.findById(pending.getCaseNum());
			if(byId.isPresent()) {
				ApplicantData data = byId.get();
				Integer appId = data.getAppId();
				Optional<UserApp> id = appRepo.findById(appId);
				if(id.isPresent()) {
					userApp = id.get();
				}

			}
			//generate pdf with elg data
			//send pdf to citizen mail

			int failed =0;

			int success =0;

			try {
				generatePdf(caseNum, userApp);
				success++;
			} catch (Exception e) {
				e.printStackTrace();
				failed++;
			}

			response.setSuccTriggers(success);
			response.setFailedTriggers(failed);	





			// store the pdf & update triggers as complete




		}


		return response;
	}

	@SuppressWarnings("unused")
	private void generatePdf(EligiblityDetermination elg, UserApp user) {

		Document document = new Document(PageSize.A4);
		FileOutputStream fos =null;
		File file = new File(elg.getCaseNum()+".pdf");

		try {
			fos = new FileOutputStream(file);
			PdfWriter.getInstance(document, fos); 
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(Color.BLUE);

		Paragraph p = new Paragraph("Eligble Reports", font);
		p.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(p); 


		PdfPTable table = new PdfPTable(7);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f ,3.0f, 1.5f});
		table.setSpacingBefore(10);

		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);

		font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);

		cell.setPhrase(new Phrase("Name", font));

		table.addCell(cell);

		cell.setPhrase(new Phrase("CaseNumber", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("planName", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("planStartDate", font));
		table.addCell(cell);

		cell.setPhrase(new Phrase("planEndDate", font));

		cell.setPhrase(new Phrase("Benfit Amount", font));
		cell.setPhrase(new Phrase("Dineal Resion", font));

		table.addCell(cell); 



		table.addCell(user.getFullName());

		table.addCell(String.valueOf(elg.getCaseNum()));

		table.addCell(elg.getPlanName());
		table.addCell( String.valueOf(elg.getPlanStartDate()));
		table.addCell(String.valueOf(elg.getPlanEndDate()));
		table.addCell(String.valueOf(elg.getBenfitAmount()));
		table.addCell(String.valueOf(elg.getDenialResion()));



		document.add(table);
		document.close();
		String subject = "HIS Eligibleity Info";
		String body = "HIS Eligiblity Info";
		emailSend.isMailSent(user.getEmail(), subject, body, file);
		updateTringger(elg.getCaseNum(), file);
	}

	@SuppressWarnings("unused")
	private void updateTringger(Integer caseNum, File file) {
		CoTriggres num = coRepo.findByCaseNum(caseNum);

		byte[] arr = new byte[(int) file.length()];
		try (FileInputStream fis = new FileInputStream(file)) {
			fis.read(arr);
			num.setCopdf(arr);
			num.setCoStatus("Completed");
			coRepo.save(num);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
