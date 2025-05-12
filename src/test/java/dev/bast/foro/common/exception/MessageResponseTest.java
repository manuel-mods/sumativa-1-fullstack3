package dev.bast.foro.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageResponseTest {

    @Test
    void testNoArgsConstructor() {
        MessageResponse messageResponse = new MessageResponse();
        assertNotNull(messageResponse);
        assertNull(messageResponse.getMessage());
    }

    @Test
    void testAllArgsConstructor() {
        MessageResponse messageResponse = new MessageResponse("Success");
        
        assertEquals("Success", messageResponse.getMessage());
    }

    @Test
    void testSettersAndGetters() {
        MessageResponse messageResponse = new MessageResponse();
        
        messageResponse.setMessage("Error occurred");
        
        assertEquals("Error occurred", messageResponse.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        MessageResponse message1 = new MessageResponse("Success");
        MessageResponse message2 = new MessageResponse("Success");
        
        assertEquals(message1, message2);
        assertEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void testNotEquals() {
        MessageResponse message1 = new MessageResponse("Success");
        MessageResponse message2 = new MessageResponse("Error");
        
        assertNotEquals(message1, message2);
    }

    @Test
    void testEqualsWithNull() {
        MessageResponse message1 = new MessageResponse(null);
        MessageResponse message2 = new MessageResponse(null);
        
        assertEquals(message1, message2);
    }

    @Test
    void testNotEqualsWithNull() {
        MessageResponse message1 = new MessageResponse("Success");
        MessageResponse message2 = new MessageResponse(null);
        
        assertNotEquals(message1, message2);
        assertNotEquals(message2, message1);
    }

    @Test
    void testToString() {
        MessageResponse messageResponse = new MessageResponse("Test message");
        
        String toString = messageResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("MessageResponse"));
        assertTrue(toString.contains("message=Test message"));
    }

    @Test
    void testEmptyMessage() {
        MessageResponse messageResponse = new MessageResponse("");
        
        assertEquals("", messageResponse.getMessage());
        assertNotNull(messageResponse.getMessage());
    }

    @Test
    void testLongMessage() {
        String longMessage = "a".repeat(1000);
        MessageResponse messageResponse = new MessageResponse(longMessage);
        
        assertEquals(longMessage, messageResponse.getMessage());
        assertEquals(1000, messageResponse.getMessage().length());
    }

    @Test
    void testSpecialCharactersInMessage() {
        String specialMessage = "Message with special characters: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        MessageResponse messageResponse = new MessageResponse(specialMessage);
        
        assertEquals(specialMessage, messageResponse.getMessage());
    }

    @Test
    void testUnicodeMessage() {
        String unicodeMessage = "Message with unicode: 你好世界 🌍 émojis 😀";
        MessageResponse messageResponse = new MessageResponse(unicodeMessage);
        
        assertEquals(unicodeMessage, messageResponse.getMessage());
    }

    @Test
    void testUpdateMessage() {
        MessageResponse messageResponse = new MessageResponse("Initial");
        
        assertEquals("Initial", messageResponse.getMessage());
        
        messageResponse.setMessage("Updated");
        assertEquals("Updated", messageResponse.getMessage());
        
        messageResponse.setMessage(null);
        assertNull(messageResponse.getMessage());
    }

    @Test
    void testHashCodeWithNull() {
        MessageResponse message1 = new MessageResponse(null);
        MessageResponse message2 = new MessageResponse(null);
        
        assertEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void testMultilineMessage() {
        String multilineMessage = "Line 1\nLine 2\nLine 3";
        MessageResponse messageResponse = new MessageResponse(multilineMessage);
        
        assertEquals(multilineMessage, messageResponse.getMessage());
        assertTrue(messageResponse.getMessage().contains("\n"));
    }
}
