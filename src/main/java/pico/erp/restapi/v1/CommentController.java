package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.comment.CommentData;
import pico.erp.comment.CommentId;
import pico.erp.comment.CommentQuery;
import pico.erp.comment.CommentRequests.AddRequest;
import pico.erp.comment.CommentRequests.RemoveRequest;
import pico.erp.comment.CommentService;
import pico.erp.comment.CommentView;
import pico.erp.comment.subject.CommentSubjectId;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("comment-controller-v1")
@RequestMapping(value = "/comment", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class CommentController {

  @Lazy
  @Autowired
  private CommentService commentService;

  @Lazy
  @Autowired
  private CommentQuery commentQuery;

  @ApiOperation(value = "댓글 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/comments")
  @PreAuthorize("isAuthenticated()")
  public CommentData add(@RequestBody AddRequest request) {
    return commentService.add(request);
  }

  @ApiOperation(value = "댓글 삭제")
  @DeleteMapping("/comments/{id}")
  @PreAuthorize("hasRole('COMMENT_MANAGER')")
  public void remove(@PathVariable("id") CommentId id) {
    commentService.remove(new RemoveRequest(id));
  }

  @ApiOperation(value = "댓글 조회")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/comments", consumes = MediaType.ALL_VALUE)
  public Page<CommentView> retrieve(@RequestParam(value = "subjectId") CommentSubjectId subjectId,
    Pageable pageable) {
    return commentQuery.retrieve(subjectId, pageable);
  }

}
