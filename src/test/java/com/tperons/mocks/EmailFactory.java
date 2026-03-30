package com.tperons.mocks;

import com.tperons.dto.request.EmailRequestDTO;

public class EmailFactory {

    private EmailFactory() {
    }

    public static EmailRequestDTO createMockEmailDTO() {
        return createMockEmailDTO(0);
    }

    public static EmailRequestDTO createMockEmailDTO(int number) {
        return new EmailRequestDTO(
                "to " + number + "@email.com",
                "Subject Test " + number,
                "Body Test " + number);
    }

}
