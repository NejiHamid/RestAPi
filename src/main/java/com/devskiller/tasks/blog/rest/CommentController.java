package com.devskiller.tasks.blog.rest;

import com.devskiller.tasks.blog.model.dto.CommentDto;
import com.devskiller.tasks.blog.model.dto.NewCommentDto;
import com.devskiller.tasks.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RestController
@RequestMapping("/posts")
public class CommentController {
	private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
	 * Ajoute un nouveau commentaire à une publication.
	 *
	 * @param postId L'ID de la publication
	 * @param commentRequest Les informations du commentaire
	 * @return Le commentaire créé ou une erreur si la publication n'existe pas
	 */
	@PostMapping("/{postId}/comments")
	public ResponseEntity<Long> addCommentToPost(@PathVariable Long postId, @RequestBody NewCommentDto commentRequest) {
		try {
			var commentId = commentService.addComment(postId, commentRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
		} catch (UnsupportedOperationException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found", e);
		}
	}

	/**
	 * Récupère tous les commentaires d'une publication.
	 *
	 * @param postId L'ID de la publication
	 * @return La liste des commentaires ou une liste vide si aucun commentaire n'est trouvé
	 */
	@GetMapping("/{postId}/comments")
	public ResponseEntity<List<CommentDto>> getCommentsForPost(@PathVariable Long postId) {
		try {
			List<CommentDto> comments = commentService.getCommentsForPost(postId);
			return ResponseEntity.ok(comments);
		} catch (UnsupportedOperationException e){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found", e);
		}
	}
}
