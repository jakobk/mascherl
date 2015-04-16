package org.mascherl.example.page;

import org.mascherl.page.MascherlPage;
import org.mascherl.validation.ValidationResult;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utils for our page classes.
 *
 * @author Jakob Korherr
 */
public class PageUtils {

    public static int parsePageParameter(String returnTo) {
        if (returnTo.contains("page=")) {
            Matcher matcher = Pattern.compile(".*\\Qpage=\\E([0-9])+.*").matcher(returnTo);
            if (matcher.find()) {
                String pageString = matcher.group(1);
                return Integer.parseInt(pageString);
            }
        }
        return 1;
    }

    public static MascherlPage determineReturnToPage(MailInboxPage mailInboxPage, String returnTo, int page) {
        if (returnTo.startsWith("/mail/sent")) {
            return mailInboxPage.sent(page);
        } else if (returnTo.startsWith("/mail/draft")) {
            return mailInboxPage.draft(page);
        } else if (returnTo.startsWith("/mail/trash")) {
            return mailInboxPage.trash(page);
        } else {
            return mailInboxPage.inbox(page);
        }
    }

    public static List<String> getValidationErrorMessages(ValidationResult validationResult) {
        return validationResult.getConstraintViolations().stream()
                .map((violation) -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                .collect(Collectors.toList());
    }

}
