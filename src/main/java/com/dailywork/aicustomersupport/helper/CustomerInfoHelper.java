package com.dailywork.aicustomersupport.helper;

import com.dailywork.aicustomersupport.dtos.ChatEntry;
import com.dailywork.aicustomersupport.model.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

@Getter
@Setter
public class CustomerInfoHelper {
    String emailAddress;
    String phoneNumber;

    public static CustomerInfo extractUserInformationChatHistory(List<ChatEntry> history){
        if(history==null || history.isEmpty()){
            return(new CustomerInfo(null,null));
        }

        Optional<String> emailAddress = history.stream()
                .filter(entry->"user".equalsIgnoreCase(entry.getRole()))
                .map(ChatEntry::getContent)
                .filter(content -> content!=null && !content.isEmpty())
                .map(CustomerInfoHelper::extractEmailAddress)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        Optional<String> phoneNumber = history.stream()
                .filter(entry->"user".equalsIgnoreCase(entry.getRole()))
                .map(ChatEntry::getContent)
                .filter(content -> content!=null && !content.isEmpty())
                .map(CustomerInfoHelper::extractPhoneNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        return new CustomerInfo(emailAddress.orElse(null),phoneNumber.orElse(null));


    }

    private static Optional<String> extractEmailAddress(String emailText){
        return Optional.ofNullable(emailText)
                .filter(s->!s.isEmpty())
                .flatMap(t->{
                    Matcher matcher= RegexPattern.EMAIl_PATTERN.matcher(t);
                    return matcher.find()?Optional.of(matcher.group(1)) :Optional.empty();
                });
    }

    private static Optional<String> extractPhoneNumber(String phoneNumber){
        return Optional.ofNullable(phoneNumber)
                .filter(s->!s.isEmpty())
                .flatMap(t->{
                    Matcher matcher= RegexPattern.PHONE_PATTERN.matcher(t);
                    return matcher.find()?Optional.of(matcher.group(1)) :Optional.empty();
                });
    }
}
