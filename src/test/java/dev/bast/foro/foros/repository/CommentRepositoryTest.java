package dev.bast.foro.foros.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class CommentRepositoryTest {

    // @Autowired
    // private CommentRepository commentRepository;
    
    // @Autowired
    // private TestEntityManager entityManager;

    // private Comment comment1;
    // private Comment comment2;
    // private Comment comment3;

    // @BeforeEach
    // void setUp() {
    //     commentRepository.deleteAll();
    //     entityManager.flush();
        
    //     comment1 = new Comment();
    //     comment1.setContent("First comment");
    //     comment1.setTopicId(1);
    //     comment1.setUserId(1L);
    //     comment1.setUsername("user1");
    //     comment1.setActive(true);
    //     comment1.setCreatedAt(LocalDateTime.now());
    //     comment1.setUpdatedAt(LocalDateTime.now());
        
    //     comment2 = new Comment();
    //     comment2.setContent("Second comment");
    //     comment2.setTopicId(1);
    //     comment2.setUserId(2L);
    //     comment2.setUsername("user2");
    //     comment2.setActive(true);
    //     comment2.setCreatedAt(LocalDateTime.now());
    //     comment2.setUpdatedAt(LocalDateTime.now());
        
    //     comment3 = new Comment();
    //     comment3.setContent("Third comment - inactive");
    //     comment3.setTopicId(2);
    //     comment3.setUserId(1L);
    //     comment3.setUsername("user1");
    //     comment3.setActive(false);
    //     comment3.setCreatedAt(LocalDateTime.now());
    //     comment3.setUpdatedAt(LocalDateTime.now());
    // }

    // @Test
    // void testSaveComment() {
    //     Comment savedComment = commentRepository.save(comment1);
        
    //     assertNotNull(savedComment.getId());
    //     assertEquals("First comment", savedComment.getContent());
    //     assertEquals(1, savedComment.getTopicId());
    //     assertEquals(1L, savedComment.getUserId());
    //     assertEquals("user1", savedComment.getUsername());
    //     assertTrue(savedComment.isActive());
    // }

    // @Test
    // void testFindByIdFound() {
    //     Comment savedComment = commentRepository.save(comment1);
    //     Optional<Comment> foundComment = commentRepository.findById(savedComment.getId().longValue());
        
    //     assertTrue(foundComment.isPresent());
    //     assertEquals("First comment", foundComment.get().getContent());
    // }

    // @Test
    // void testFindByIdNotFound() {
    //     Optional<Comment> foundComment = commentRepository.findById(999L);
    //     assertFalse(foundComment.isPresent());
    // }

    // @Test
    // void testFindByTopicIdAndActiveIsTrue() {
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
    //     commentRepository.save(comment3);
        
    //     List<Comment> activeComments = commentRepository.findByTopicIdAndActiveIsTrue(1L);
        
    //     assertEquals(2, activeComments.size());
    //     assertTrue(activeComments.stream().allMatch(Comment::isActive));
    //     assertTrue(activeComments.stream().allMatch(c -> c.getTopicId() == 1));
    // }

    // @Test
    // void testFindByTopicIdAndActiveTrue() {
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
    //     commentRepository.save(comment3);
        
    //     List<Comment> activeComments = commentRepository.findByTopicIdAndActiveTrue(1L);
        
    //     assertEquals(2, activeComments.size());
    //     assertTrue(activeComments.stream().allMatch(Comment::isActive));
    //     assertTrue(activeComments.stream().allMatch(c -> c.getTopicId() == 1));
    // }

    // @Test
    // void testFindByUserIdAndActiveIsTrue() {
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
    //     commentRepository.save(comment3);
        
    //     List<Comment> userActiveComments = commentRepository.findByUserIdAndActiveIsTrue(1L);
        
    //     assertEquals(1, userActiveComments.size());
    //     assertEquals("First comment", userActiveComments.get(0).getContent());
    //     assertTrue(userActiveComments.get(0).isActive());
    // }

    // @Test
    // void testFindByTopicId() {
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
    //     commentRepository.save(comment3);
        
    //     List<Comment> topicComments = commentRepository.findByTopicId(1L);
        
    //     assertEquals(2, topicComments.size());
    //     assertTrue(topicComments.stream().allMatch(c -> c.getTopicId() == 1));
    // }

    // @Test
    // void testUpdateComment() {
    //     Comment savedComment = commentRepository.save(comment1);
    //     savedComment.setContent("Updated content");
        
    //     Comment updatedComment = commentRepository.save(savedComment);
        
    //     assertEquals(savedComment.getId(), updatedComment.getId());
    //     assertEquals("Updated content", updatedComment.getContent());
    // }

    // @Test
    // void testDeleteComment() {
    //     Comment savedComment = commentRepository.save(comment1);
    //     Long savedId = savedComment.getId().longValue();
        
    //     commentRepository.deleteById(savedId);
        
    //     Optional<Comment> deletedComment = commentRepository.findById(savedId);
    //     assertFalse(deletedComment.isPresent());
    // }

    // @Test
    // void testFindAll() {
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
    //     commentRepository.save(comment3);
        
    //     List<Comment> allComments = commentRepository.findAll();
        
    //     assertEquals(3, allComments.size());
    // }

    // @Test
    // void testCount() {
    //     assertEquals(0, commentRepository.count());
        
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
        
    //     assertEquals(2, commentRepository.count());
    // }

    // @Test
    // void testDeleteAll() {
    //     commentRepository.save(comment1);
    //     commentRepository.save(comment2);
    //     assertEquals(2, commentRepository.count());
        
    //     commentRepository.deleteAll();
        
    //     assertEquals(0, commentRepository.count());
    // }

    // @Test
    // void testExistsById() {
    //     Comment savedComment = commentRepository.save(comment1);
        
    //     assertTrue(commentRepository.existsById(savedComment.getId().longValue()));
    //     assertFalse(commentRepository.existsById(999L));
    // }

    // @Test
    // void testCustomQueriesReturnEmptyWhenNoMatch() {
    //     // Save comments but query for non-existent data
    //     commentRepository.save(comment1);
        
    //     List<Comment> noMatchTopic = commentRepository.findByTopicIdAndActiveIsTrue(999L);
    //     assertTrue(noMatchTopic.isEmpty());
        
    //     List<Comment> noMatchUser = commentRepository.findByUserIdAndActiveIsTrue(999L);
    //     assertTrue(noMatchUser.isEmpty());
        
    //     List<Comment> noMatchTopicAll = commentRepository.findByTopicId(999L);
    //     assertTrue(noMatchTopicAll.isEmpty());
    // }

    // @Test
    // void testTimestamps() {
    //     LocalDateTime before = LocalDateTime.now();
    //     Comment savedComment = commentRepository.save(comment1);
    //     LocalDateTime after = LocalDateTime.now();
        
    //     assertNotNull(savedComment.getCreatedAt());
    //     assertNotNull(savedComment.getUpdatedAt());
        
    //     // The timestamps should be between before and after
    //     assertTrue(savedComment.getCreatedAt().isAfter(before.minusSeconds(1)));
    //     assertTrue(savedComment.getCreatedAt().isBefore(after.plusSeconds(1)));
    // }

    // @Test
    // void testRepositoryIsInterface() {
    //     assertTrue(CommentRepository.class.isInterface());
    //     assertTrue(commentRepository instanceof CommentRepository);
    // }
}
