package dev.bast.foro.foros.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class TopicRepositoryTest {

    // @Autowired
    // private TopicRepository topicRepository;
    
    // @Autowired
    // private TestEntityManager entityManager;

    // private Topic topic1;
    // private Topic topic2;
    // private Topic topic3;

    // @BeforeEach
    // void setUp() {
    //     topicRepository.deleteAll();
    //     entityManager.flush();
        
    //     topic1 = new Topic();
    //     topic1.setTitle("First Topic");
    //     topic1.setContent("This is the content of the first topic");
    //     topic1.setUserId(1L);
    //     topic1.setUsername("user1");
    //     topic1.setActive(true);
    //     topic1.setCreatedAt(LocalDateTime.now());
    //     topic1.setUpdatedAt(LocalDateTime.now());
        
    //     topic2 = new Topic();
    //     topic2.setTitle("Second Topic");
    //     topic2.setContent("This is the content of the second topic");
    //     topic2.setUserId(2L);
    //     topic2.setUsername("user2");
    //     topic2.setActive(true);
    //     topic2.setCreatedAt(LocalDateTime.now());
    //     topic2.setUpdatedAt(LocalDateTime.now());
        
    //     topic3 = new Topic();
    //     topic3.setTitle("Third Topic - Inactive");
    //     topic3.setContent("This is the content of the third topic");
    //     topic3.setUserId(1L);
    //     topic3.setUsername("user1");
    //     topic3.setActive(false);
    //     topic3.setCreatedAt(LocalDateTime.now());
    //     topic3.setUpdatedAt(LocalDateTime.now());
    // }

    // @Test
    // void testSaveTopic() {
    //     Topic savedTopic = topicRepository.save(topic1);
        
    //     assertNotNull(savedTopic.getId());
    //     assertEquals("First Topic", savedTopic.getTitle());
    //     assertEquals("This is the content of the first topic", savedTopic.getContent());
    //     assertEquals(1L, savedTopic.getUserId());
    //     assertEquals("user1", savedTopic.getUsername());
    //     assertTrue(savedTopic.isActive());
    // }

    // @Test
    // void testFindByIdFound() {
    //     Topic savedTopic = topicRepository.save(topic1);
    //     Optional<Topic> foundTopic = topicRepository.findById(savedTopic.getId().longValue());
        
    //     assertTrue(foundTopic.isPresent());
    //     assertEquals("First Topic", foundTopic.get().getTitle());
    // }

    // @Test
    // void testFindByIdNotFound() {
    //     Optional<Topic> foundTopic = topicRepository.findById(999L);
    //     assertFalse(foundTopic.isPresent());
    // }

    // @Test
    // void testFindByUserIdAndActiveIsTrue() {
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
    //     topicRepository.save(topic3);
        
    //     List<Topic> userActiveTopics = topicRepository.findByUserIdAndActiveIsTrue(1L);
        
    //     assertEquals(1, userActiveTopics.size());
    //     assertEquals("First Topic", userActiveTopics.get(0).getTitle());
    //     assertTrue(userActiveTopics.get(0).isActive());
    // }

    // @Test
    // void testFindByUserIdAndActiveTrue() {
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
    //     topicRepository.save(topic3);
        
    //     List<Topic> userActiveTopics = topicRepository.findByUserIdAndActiveTrue(1L);
        
    //     assertEquals(1, userActiveTopics.size());
    //     assertEquals("First Topic", userActiveTopics.get(0).getTitle());
    //     assertTrue(userActiveTopics.get(0).isActive());
    // }

    // @Test
    // void testFindByActiveIsTrue() {
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
    //     topicRepository.save(topic3);
        
    //     List<Topic> activeTopics = topicRepository.findByActiveIsTrue();
        
    //     assertEquals(2, activeTopics.size());
    //     assertTrue(activeTopics.stream().allMatch(Topic::isActive));
    // }

    // @Test
    // void testUpdateTopic() {
    //     Topic savedTopic = topicRepository.save(topic1);
    //     savedTopic.setTitle("Updated Title");
    //     savedTopic.setContent("Updated content");
        
    //     Topic updatedTopic = topicRepository.save(savedTopic);
        
    //     assertEquals(savedTopic.getId(), updatedTopic.getId());
    //     assertEquals("Updated Title", updatedTopic.getTitle());
    //     assertEquals("Updated content", updatedTopic.getContent());
    // }

    // @Test
    // void testDeleteTopic() {
    //     Topic savedTopic = topicRepository.save(topic1);
    //     Long savedId = savedTopic.getId().longValue();
        
    //     topicRepository.deleteById(savedId);
        
    //     Optional<Topic> deletedTopic = topicRepository.findById(savedId);
    //     assertFalse(deletedTopic.isPresent());
    // }

    // @Test
    // void testFindAll() {
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
    //     topicRepository.save(topic3);
        
    //     List<Topic> allTopics = topicRepository.findAll();
        
    //     assertEquals(3, allTopics.size());
    // }

    // @Test
    // void testCount() {
    //     assertEquals(0, topicRepository.count());
        
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
        
    //     assertEquals(2, topicRepository.count());
    // }

    // @Test
    // void testDeleteAll() {
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
    //     assertEquals(2, topicRepository.count());
        
    //     topicRepository.deleteAll();
        
    //     assertEquals(0, topicRepository.count());
    // }

    // @Test
    // void testExistsById() {
    //     Topic savedTopic = topicRepository.save(topic1);
        
    //     assertTrue(topicRepository.existsById(savedTopic.getId().longValue()));
    //     assertFalse(topicRepository.existsById(999L));
    // }

    // @Test
    // void testCustomQueriesReturnEmptyWhenNoMatch() {
    //     // Save topics but query for non-existent data
    //     topicRepository.save(topic1);
        
    //     List<Topic> noMatchUser = topicRepository.findByUserIdAndActiveIsTrue(999L);
    //     assertTrue(noMatchUser.isEmpty());
        
    //     List<Topic> noMatchUserActiveTrue = topicRepository.findByUserIdAndActiveTrue(999L);
    //     assertTrue(noMatchUserActiveTrue.isEmpty());
    // }

    // @Test
    // void testFindByActiveIsTrueReturnsEmptyWhenNoActive() {
    //     // Save only inactive topics
    //     topic1.setActive(false);
    //     topic2.setActive(false);
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
        
    //     List<Topic> activeTopics = topicRepository.findByActiveIsTrue();
    //     assertTrue(activeTopics.isEmpty());
    // }

    // @Test
    // void testSaveMultipleTopics() {
    //     List<Topic> topics = List.of(topic1, topic2, topic3);
    //     List<Topic> savedTopics = topicRepository.saveAll(topics);
        
    //     assertEquals(3, savedTopics.size());
    //     assertTrue(savedTopics.stream().allMatch(t -> t.getId() != null));
    // }

    // @Test
    // void testTimestamps() {
    //     LocalDateTime before = LocalDateTime.now();
    //     Topic savedTopic = topicRepository.save(topic1);
    //     LocalDateTime after = LocalDateTime.now();
        
    //     assertNotNull(savedTopic.getCreatedAt());
    //     assertNotNull(savedTopic.getUpdatedAt());
        
    //     // The timestamps should be between before and after
    //     assertTrue(savedTopic.getCreatedAt().isAfter(before.minusSeconds(1)));
    //     assertTrue(savedTopic.getCreatedAt().isBefore(after.plusSeconds(1)));
    // }

    // @Test
    // void testFindByUserIdWithMultipleTopics() {
    //     // User 1 has two topics (one active, one inactive)
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic3);
        
    //     // User 2 has one active topic
    //     topicRepository.save(topic2);
        
    //     List<Topic> user1ActiveTopics = topicRepository.findByUserIdAndActiveIsTrue(1L);
    //     assertEquals(1, user1ActiveTopics.size());
        
    //     List<Topic> user2ActiveTopics = topicRepository.findByUserIdAndActiveIsTrue(2L);
    //     assertEquals(1, user2ActiveTopics.size());
    // }

    // @Test
    // void testRepositoryIsInterface() {
    //     assertTrue(TopicRepository.class.isInterface());
    //     assertTrue(topicRepository instanceof TopicRepository);
    // }

    // @Test
    // void testFindByActiveIsTrueOrderByCreatedAt() {
    //     // Create topics with different creation times
    //     topic1.setCreatedAt(LocalDateTime.now().minusDays(2));
    //     topic2.setCreatedAt(LocalDateTime.now().minusDays(1));
        
    //     topicRepository.save(topic1);
    //     topicRepository.save(topic2);
    //     topicRepository.save(topic3); // inactive
        
    //     List<Topic> activeTopics = topicRepository.findByActiveIsTrue();
        
    //     assertEquals(2, activeTopics.size());
    //     // Verify the results contain the active topics
    //     assertTrue(activeTopics.stream()
    //             .allMatch(t -> t.isActive() && (t.getTitle().equals("First Topic") || t.getTitle().equals("Second Topic"))));
    // }
}
