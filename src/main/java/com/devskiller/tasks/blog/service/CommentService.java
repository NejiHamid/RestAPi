package com.devskiller.tasks.blog.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.devskiller.tasks.blog.model.Comment;
import com.devskiller.tasks.blog.repository.CommentRepository;
import com.devskiller.tasks.blog.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devskiller.tasks.blog.model.dto.CommentDto;
import com.devskiller.tasks.blog.model.dto.NewCommentDto;

@Service
@Transactional
public class CommentService {
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    /**
	 * Returns a list of all comments for a blog post with passed id.
	 *
	 * @param postId id of the post
	 * @return list of comments sorted by creation date descending - most recent first
	 */
	public List<CommentDto> getCommentsForPost(Long postId) {
		return postRepository.findById(postId)
			.map(post -> mapCommentsToDto(post.getComments()))
			.orElseThrow(() -> new UnsupportedOperationException("Post not found with id " + postId));
	}

	private List<CommentDto> mapCommentsToDto(List<Comment> comments) {
		return comments.stream()
			.sorted(Comparator.comparing(Comment::getCreationDate).reversed()) // Sort by descending date
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}

	private CommentDto convertToDto(Comment comment) {
		return new CommentDto(
			comment.getId(),
			comment.getContent(),
			comment.getAuthor(),
			comment.getCreationDate()
		);
	}

	/**
	 * Creates a new comment
	 *
	 * @param postId id of the post
	 * @param newCommentDto data of new comment
	 * @return id of the created comment
	 *
	 * @throws IllegalArgumentException if postId is null or there is no blog post for passed postId
	 */
	public Long addComment(Long postId, NewCommentDto newCommentDto) {
		return postRepository.findById(postId).map(post -> {
			Comment comment = new Comment();
			comment.setContent(newCommentDto.content());
			comment.setAuthor(newCommentDto.author());
			comment.setPost(post);
			comment.setCreationDate(LocalDateTime.now());
			post.getComments().add(comment);
			commentRepository.save(comment);
			return comment.getId(); // Return the ID of the saved comment
		}).orElseThrow(() -> new UnsupportedOperationException("Post not found with id " + postId));
	}

}
