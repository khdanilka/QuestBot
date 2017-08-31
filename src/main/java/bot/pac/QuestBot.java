package bot.pac;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.database.BDConnect;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.toIntExact;


public class QuestBot extends TelegramLongPollingBot {

    private BDConnect bd;
    private ReplyKeyboardMarkup keyboardMarkup;

    static HashMap<Long,UserClass> users;


    public static HashMap<Long, UserClass> getUsers() {
        return users;
    }

    public QuestBot(BDConnect bd){
        this.bd = bd;
        initReplykeyboard();
        users = new HashMap<>();
    }

    private static final HashMap<String, String> myMap;
    static
    {
        myMap = new HashMap<>();
        myMap.put("C актером", "actor");
        myMap.put("Перфоманс", "perfomans");
        myMap.put("Страшные", "horror");
        myMap.put("Детские", "child");
        myMap.put("На логику", "logic");
        myMap.put("Акция", "discount");

    }

    private void initReplykeyboard(){

        keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        String act = EmojiParser.parseToUnicode("C актером :eyes:");
        row.add(act);
        act = EmojiParser.parseToUnicode("Перфоманс :ghost:");
        row.add(act);
        keyboard.add(row);

        row = new KeyboardRow();

        act = EmojiParser.parseToUnicode("Страшные :scream:");
        row.add(act);
        act = EmojiParser.parseToUnicode("Детские :family:");
        row.add(act);
        keyboard.add(row);

        row = new KeyboardRow();
        act = EmojiParser.parseToUnicode("На логику :mortar_board:");
        row.add(act);
        act = EmojiParser.parseToUnicode("Акция :gift:");
        row.add(act);

        keyboard.add(row);

        row = new KeyboardRow();
        act = EmojiParser.parseToUnicode("Избранное :star:");
        row.add(act);
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);

    }


    @Override
    public String getBotUsername() {
        // TODO
        return "questoman_bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "442173995:AAGRPd7iv5EAFP1SuAaIV1fwjbtlMn6vYJE";
    }

    public void onUpdateReceived(Update update) {

        UserClass currentUser= new UserClass();

        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String user_first_name = update.getMessage().getChat().getFirstName();
            String user_last_name = update.getMessage().getChat().getLastName();
            String user_username = update.getMessage().getChat().getUserName();
            long user_id = update.getMessage().getChat().getId();

            if (users.get(user_id) == null) userInit(new UserClass(user_id,
                    user_first_name,
                    user_last_name,
                    user_username));

            currentUser = users.get(user_id);
            currentUser.setLastActiveTime(System.currentTimeMillis());

            if (message_text.equals("/start") || message_text.startsWith("Главная")) {
                currentUser.setCondition(0);
                SendMessage message = new SendMessage()
                        .setChatId(currentUser.getUser_id())
                        //.setParseMode("HTML")
                        .setText("Выберите интересующую вас категорию");
                message.setReplyMarkup(keyboardMarkup);
                try {
                    sendMessage(message);

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                log(user_first_name, user_last_name, Long.toString(user_id), message_text, message.getText());

            } else if (message_text.startsWith("C актером") || message_text.startsWith("Перфоманс") || message_text.startsWith("Страшные") ||
                    message_text.startsWith("Детские")|| message_text.startsWith("На логику") || message_text.startsWith("Акция")) {

                currentUser.setIndex(-1);
                if (currentUser.getMessage_id() != 0) {
                    //System.out.println("До удадления " +  currentUser);

                    DeleteMessage new_message = new DeleteMessage()
                            .setChatId(Long.toString(currentUser.getUser_id()))
                            .setMessageId(toIntExact(currentUser.getMessage_id()));
                    try {
                        deleteMessage(new_message);
                        currentUser.setMessage_id(0);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                SendMessage msg = new SendMessage() // Create a message object object
                        .setChatId(currentUser.getUser_id())
                        .setText("Выберите квест");

                currentUser.setCurrentCategory(EmojiParser.removeAllEmojis(message_text).trim());

                questInit(currentUser);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

//                for(QuestClass q: currentUser.quests.values()) {
//                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
//                    rowInline.add(new InlineKeyboardButton().setText(q.getName()).setCallbackData("@questname::" +  q.hashCode()));
//                    rowsInline.add(rowInline);
//                }

                int offset=0;
                if (currentUser.quests.size() > 5 ) offset = 5;
                else offset = currentUser.quests.size();
                for(int i=0; i < offset; i++){
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(currentUser.quests.get(i).getName()).setCallbackData("@questname::" +  i));
                    rowsInline.add(rowInline);
                }

                if ((offset >= 5)) {
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    String nxt = EmojiParser.parseToUnicode("Следующий :fast_forward:");
                    rowInline.add(new InlineKeyboardButton().setText(nxt).setCallbackData("@next::" + offset));
                    rowsInline.add(rowInline);
                }

                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                msg.setReplyMarkup(markupInline);


                try {
                    Message lastInlineKeyboard;
                    lastInlineKeyboard = sendMessage(msg);
                    currentUser.setMessage_id(lastInlineKeyboard.getMessageId());

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                log(user_first_name, user_last_name, Long.toString(user_id), message_text, "quests");

            } else if (message_text.startsWith("Еще '")) {
                // User send /start

                String str = EmojiParser.parseToUnicode(":wink::thumbsup: ");

                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(str)
                        .setReplyMarkup(keyboardMarkup);
                try {
                    sendMessage(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                if (currentUser.quests == null || currentUser.quests.isEmpty())  questInit(currentUser);
                //questInit(currentUser);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                int offset=0;
                if (currentUser.quests.size() > 5 ) offset = 5;
                else offset = currentUser.quests.size();
                for(int i=0; i < offset; i++){
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(currentUser.quests.get(i).getName()).setCallbackData("@questname::" +  i));
                    rowsInline.add(rowInline);
                }

                if (offset >= 5) {
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    String nxt = EmojiParser.parseToUnicode("Следующий :fast_forward:");
                    rowInline.add(new InlineKeyboardButton().setText(nxt).setCallbackData("@next::" + offset));
                    rowsInline.add(rowInline);
                }

                // log(user_first_name, user_last_name, Long.toString(user_id), message_text, message.getText());
                markupInline.setKeyboard(rowsInline);
                message.setReplyMarkup(markupInline);

                message.setText("Выберите квест");
                try {
                    Message lastInlineKeyboard;
                    lastInlineKeyboard = sendMessage(message);
                    currentUser.setMessage_id(lastInlineKeyboard.getMessageId());

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (message_text.startsWith("Контакты")) {

                if (currentUser.quests == null || currentUser.quests.isEmpty())  questInit(currentUser);
                QuestClass lastQuest = currentUser.quests.get(currentUser.getIndex());

                String str = lastQuest.getContacts();
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(currentUser.getUser_id())
                        .setText(str);
                //log(user_first_name, user_last_name, Long.toString(user_id), message_text, "wtf");
                try {
                    sendMessage(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (message_text.startsWith("На карте")) {

                if (currentUser.quests == null || currentUser.quests.isEmpty())  questInit(currentUser);

                QuestClass lastQuest = currentUser.quests.get(currentUser.getIndex());

                String[] str = lastQuest.getLocation().split("::");

                SendLocation location = new SendLocation() // Create a message object object
                        .setChatId(currentUser.getUser_id())
                        .setLatitude(Float.valueOf(str[0]))
                        .setLongitude(Float.valueOf(str[1]));
                //log(user_first_name, user_last_name, Long.toString(user_id), message_text, "wtf");
                try {
                    sendLocation(location); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else {
                // Unknown command
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText("Unknown command");
                log(user_first_name, user_last_name, Long.toString(user_id), message_text, "wtf");
                try {
                    sendMessage(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        } else if (update.hasCallbackQuery()) {
            // Set variables

            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long user_id = update.getCallbackQuery().getMessage().getChatId();
            String user_first_name = update.getCallbackQuery().getMessage().getChat().getFirstName();
            String user_last_name = update.getCallbackQuery().getMessage().getChat().getLastName();
            String user_username = update.getCallbackQuery().getMessage().getChat().getUserName();

            if (users.get(user_id) == null) userInit(new UserClass(user_id,
                    user_first_name,
                    user_last_name,
                    user_username));

            currentUser = users.get(user_id);
            currentUser.setLastActiveTime(System.currentTimeMillis());
            //currentUser.lock.lock();

            if (currentUser.quests == null || currentUser.quests.isEmpty()) questInit(currentUser);

            if (call_data.startsWith("@questname")) {

                String[] strName = call_data.split("::");
                QuestClass lastQuest = currentUser.quests.get(Integer.valueOf(strName[1]));
                currentUser.setIndex(Integer.valueOf(strName[1]));

                EditMessageText new_message = new EditMessageText()
                        .setChatId(currentUser.getUser_id())
                        .setMessageId(toIntExact(currentUser.getMessage_id()))
                        .setParseMode("HTML")
                        .setText(EmojiParser.parseToUnicode(lastQuest.getDescription()));
                //System.out.println("Перед тем как обновить сообщение" +  currentUser);
                try {
                    editMessageText(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                SendPhoto msg = new SendPhoto()
                        .setChatId(currentUser.getUser_id())
                        .setPhoto(lastQuest.getPhoto());

                ReplyKeyboardMarkup keyboardMarkup1 = new ReplyKeyboardMarkup();
                // Create the keyboard (list of keyboard rows)
                keyboardMarkup1.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                // Create a keyboard row
                KeyboardRow row = new KeyboardRow();

                String str = EmojiParser.parseToUnicode("На карте :earth_asia:");
                row.add(str);
                str = EmojiParser.parseToUnicode("Контакты :phone:");
                row.add(str);
                keyboard.add(row);

                row = new KeyboardRow();
                str = EmojiParser.parseToUnicode("Добавить в избранное :star:");
                row.add(str);
                keyboard.add(row);

                row = new KeyboardRow();
                str = EmojiParser.parseToUnicode("Главная :house:");
                row.add(str);
                str = EmojiParser.parseToUnicode("Еще '" + currentUser.getCurrentCategory() + "' :leftwards_arrow_with_hook:");
                row.add(str);
                keyboard.add(row);

                keyboardMarkup1.setKeyboard(keyboard);
                msg.setReplyMarkup(keyboardMarkup1);
                //  log(user_first_name, user_last_name, Long.toString(user_id), message_text, message.getText());

                try {
                    sendPhoto(msg); // Call method to send the photo with caption
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
            else if (call_data.startsWith("@next")) {

                String[] strName = call_data.split("::");
                int stV = 0;
                stV = Integer.valueOf(strName[1]);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                int offset = 0;
                if (currentUser.quests.size() > 5 + stV ) offset = 5 + stV;
                else offset = currentUser.quests.size();
                for(int i=stV; i < offset; i++){
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(currentUser.quests.get(i).getName()).setCallbackData("@questname::" +  i));
                    rowsInline.add(rowInline);
                }

                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                if ((offset == currentUser.quests.size())) {
                    int prevStartV = stV - 5;
                    String prev = EmojiParser.parseToUnicode(":rewind: Предыдущий ");
                    rowInline.add(new InlineKeyboardButton().setText(prev).setCallbackData("@next::" + prevStartV));
                } else if (stV == 0 ) {
                    String nxt = EmojiParser.parseToUnicode("Следующий :fast_forward:");
                    int nextStv = offset;
                    rowInline.add(new InlineKeyboardButton().setText(nxt).setCallbackData("@next::" + nextStv));
                } else {
                    int prevStartV = stV - 5;
                    String prev = EmojiParser.parseToUnicode(":rewind: Предыдущий");
                    rowInline.add(new InlineKeyboardButton().setText(prev).setCallbackData("@next::" + prevStartV));

                    String nxt = EmojiParser.parseToUnicode("Следующий :fast_forward:");
                    int nextStv = offset;
                    rowInline.add(new InlineKeyboardButton().setText(nxt).setCallbackData("@next::" + nextStv));

                }
                rowsInline.add(rowInline);


                EditMessageText new_message = new EditMessageText()
                        .setChatId(currentUser.getUser_id())
                        .setMessageId(toIntExact(currentUser.getMessage_id()))
                        .setText("Выберите квест");
                //System.out.println("Перед тем как обновить сообщение" +  currentUser);

                markupInline.setKeyboard(rowsInline);
                new_message.setReplyMarkup(markupInline);

                try {
                    editMessageText(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
        }
        //System.out.println("После снятие лок" +  currentUser);
        //currentUser.lock.unlock();

    }

//
//    @Override
//    public void onUpdateReceived(Update update) {
//
//        // We check if the update has a message and the message has text
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            // Set variables
//            String message_text = update.getMessage().getText();
//            long chat_id = update.getMessage().getChatId();
//
//            SendMessage message = new SendMessage() // Create a message object object
//                    .setChatId(chat_id)
//                    .setText(message_text);
//            try {
//                sendMessage(message); // Sending our message object to user
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//        else if (update.hasMessage() && update.getMessage().hasPhoto()) {
//            // Message contains photo
//            // Set variables
//            long chat_id = update.getMessage().getChatId();
//
//            // Array with photo objects with different sizes
//            // We will get the biggest photo from that array
//            List<PhotoSize> photos = update.getMessage().getPhoto();
//            // Know file_id
//            String f_id = photos.stream()
//                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
//                    .findFirst()
//                    .orElse(null).getFileId();
//            // Know photo width
//            int f_width = photos.stream()
//                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
//                    .findFirst()
//                    .orElse(null).getWidth();
//            // Know photo height
//            int f_height = photos.stream()
//                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
//                    .findFirst()
//                    .orElse(null).getHeight();
//            // Set photo caption
//            String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
//            SendPhoto msg = new SendPhoto()
//                    .setChatId(chat_id)
//                    .setPhoto(f_id)
//                    .setCaption(caption);
//            try {
//                sendPhoto(msg); // Call method to send the photo with caption
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    private void userInit(UserClass newUser) {

        bd.init();
        UserClass bdUser = bd.getUserFromBD(newUser.getUser_id());
        bd.dispose();

        if ((bdUser) == null) users.put(newUser.getUser_id(),newUser);
        else users.put(bdUser.getUser_id(),bdUser);

    }

    private void questInit(UserClass currentUser) {

        bd.init();
        ArrayList<QuestClass> arrQ = bd.getQuestListWithTag(myMap.get(currentUser.getCurrentCategory()));
        bd.dispose();
        System.out.println(currentUser);

        currentUser.setQuestsFromArrayList(arrQ);
    }


    private void log(String first_name, String last_name, String user_id, String txt, String bot_answer) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (id = " + user_id + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
    }

}