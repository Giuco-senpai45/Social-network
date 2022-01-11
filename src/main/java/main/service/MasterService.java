package main.service;

import main.domain.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MasterService {

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private PostService postService;
    private Iterable<Message> messages;

    public MasterService(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, PostService postService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.postService = postService;
    }

    private void generatePDF(String fileName, String title1, String title2, String title3, List<String> messages, Boolean append){
        try {
            File file = new File(fileName);
            PDDocument document = PDDocument.load(file);
            PDPage page;
            PDPageContentStream contentStream;
            if(!append) {
                while(document.getNumberOfPages()>0)
                    document.removePage(0);
                document.save(fileName);
                document.addPage(new PDPage());
                page = document.getPage(0);
                contentStream = new PDPageContentStream(document, page);
            }
            else{
                document.addPage(new PDPage());
                page = document.getPage(document.getNumberOfPages()-1);
                contentStream = new PDPageContentStream(document, page);
            }
            contentStream.beginText();
            contentStream.setFont( PDType1Font.TIMES_BOLD, 22);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(45, 720);
            contentStream.showText(title1);
            contentStream.newLine();
            contentStream.newLine();
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont( PDType1Font.TIMES_BOLD, 18);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(45, 700);
            contentStream.showText(title2);
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText(title3);
            contentStream.newLine();
            contentStream.newLine();
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont( PDType1Font.TIMES_ROMAN, 16 );
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(45, 650);
            for(String text1: messages) {
                List<String> text2 = splitText(text1, page);
                for(String text3: text2) {
                    contentStream.showText(text3);
                    contentStream.newLine();
                }
                contentStream.newLine();
            }
            contentStream.endText();
            contentStream.close();
            document.save(fileName);
            document.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void generateFirstPDF(Long loggedUser, LocalDateTime beginningDate, LocalDateTime endDate){
        String fileName = "E:\\FMI\\Anul II\\Semestrul I\\MAP\\Lab5\\Social-network\\reports\\report1.pdf";
        try {
            PDDocument document = new PDDocument();
            document.save(fileName);
            document.close();
        }catch (IOException e){

        }
        String title1 = "Logged user: " + getUserService().findUserById(loggedUser).getFirstName() + " " + getUserService().findUserById(loggedUser).getLastName();
        String title2 = "Activity: Messages";
        String title3 = beginningDate.toString() + " --- " + endDate.toString();
        List<String> messages = new ArrayList<>();
        for(ChatDTO m: messageService.report12(loggedUser, beginningDate, endDate)) {
            String text1 = getUserService().findUserById(m.getUserID()).getFirstName() + " " + getUserService().findUserById(m.getUserID()).getLastName() + ", " + m.getTimestamp() + ": " + m.getMessage();
            text1 = text1.replace("\n", "");
            messages.add(text1);
        }
        generatePDF(fileName, title1, title2, title3, messages, false);

        title1 = "Logged user: " + getUserService().findUserById(loggedUser).getFirstName() + " " + getUserService().findUserById(loggedUser).getLastName();
        title2 = "Activity: New Friends";
        List<String> friends = new ArrayList<>();
        for(UserFriendshipsDTO f: userService.report11(loggedUser, beginningDate, endDate)) {
            String text1 = f.getFriendFirstName() + " " + f.getFriendLastName() + ": " + f.getDate();
            text1 = text1.replace("\n", "");
            friends.add(text1);
        }
        generatePDF(fileName, title1, title2, title3, friends, true);
    }

    public void generateSecondPDF(Long loggedUser, Long fromID, LocalDateTime beginningDate, LocalDateTime endDate){
        String filename = "E:\\FMI\\Anul II\\Semestrul I\\MAP\\Lab5\\Social-network\\reports\\report2.pdf";
        try {
            PDDocument document = new PDDocument();
            document.save(filename);
            document.close();
        }catch (IOException e){

        }
        String title = "Messages from " + getUserService().findUserById(fromID).getFirstName() + " " + getUserService().findUserById(fromID).getFirstName() + ": ";
        String title2 = beginningDate.toString() + " --- " + endDate.toString();
        List<String> messages = new ArrayList<>();
        for(ChatDTO m: messageService.report2(loggedUser,fromID, beginningDate, endDate)) {
            String text1 = m.getTimestamp() + ": " + m.getMessage();
            text1 = text1.replace("\n", "");
            messages.add(text1);
        }
        generatePDF(filename, title, title2, "",  messages, false);
    }

    private List<String> splitText(String text, PDPage page) throws IOException {
        PDRectangle mediabox = page.getMediaBox();
        float margin = 72;
        float width = mediabox.getWidth() - 2*margin;
        float startX = mediabox.getLowerLeftX() + margin;
        float startY = mediabox.getUpperRightY() - margin;
        PDFont pdfFont = PDType1Font.TIMES_ROMAN;

        List<String> lines = new ArrayList<String>();
        int lastSpace = -1;
        while (text.length() > 0)
        {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);
            float size = 16 * pdfFont.getStringWidth(subString) / 1000;
            if (size > width)
            {
                if (lastSpace < 0)
                    lastSpace = spaceIndex;
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            }
            else if (spaceIndex == text.length())
            {
                lines.add(text);
                text = "";
            }
            else
            {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }


    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public FriendRequestService getFriendRequestService() {
        return friendRequestService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public PostService getPostService() {
        return postService;
    }
}
