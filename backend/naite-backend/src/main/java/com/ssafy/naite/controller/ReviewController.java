package com.ssafy.naite.controller;

import com.ssafy.naite.dto.review.ReviewDto;
import com.ssafy.naite.service.review.ReviewService;
import com.ssafy.naite.service.user.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"*"}, maxAge = 6000)
@RestController
@RequestMapping("/review")
@AllArgsConstructor
@Api(value = "리뷰 게시판")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    /**
     * 리뷰 게시글 전체 조회
     */
    @GetMapping("/list")
    @ApiOperation(value = "리뷰 게시글 전체 조회")
    public ResponseEntity<List<ReviewDto.ReviewResponseDto>> findAllReviews() {
        List<ReviewDto.ReviewResponseDto> reviewResponseDtoList = reviewService.findAllReviews();
        return new ResponseEntity<List<ReviewDto.ReviewResponseDto>>(reviewResponseDtoList, HttpStatus.OK);
    }

    /**
     * 리뷰 게시글 카테고리별 조회
     */
    @GetMapping("/list/{small_category_no}")
    @ApiOperation(value = "리뷰 게시글 카테고리별 조회")
    public ResponseEntity<List<ReviewDto.ReviewResponseDto>> findAllReviewsByCategory(@PathVariable int small_category_no) {
        List<ReviewDto.ReviewResponseDto> reviewResponseDtoList = reviewService.findAllReviewsByCategory(small_category_no);
        return new ResponseEntity<List<ReviewDto.ReviewResponseDto>>(reviewResponseDtoList, HttpStatus.OK);
    }

    /**
     * 리뷰 게시글 상세 조회
     */
    @GetMapping("/detail/{review_no}")
    @ApiOperation(value = "리뷰 게시글 상세 조회")
    public ResponseEntity<ReviewDto.ReviewResponseDto> findReviewById(@PathVariable int review_no) {
        ReviewDto.ReviewResponseDto reviewResponseDto = reviewService.findReviewById(review_no);
        return new ResponseEntity<ReviewDto.ReviewResponseDto>(reviewResponseDto, HttpStatus.OK);
    }

    /**
     * 리뷰 게시글 등록
     */
    @PostMapping("/insert")
    @ApiOperation(value = "리뷰 게시글 등록")
    public ResponseEntity<Integer> insertReview(@RequestBody ReviewDto.ReviewSaveRequestDto reviewSaveRequestDto, HttpServletRequest req) {
        int userNo = getUserNo(req);
        int insertedReviewNo = reviewService.insertReview(reviewSaveRequestDto, userNo);
        return new ResponseEntity<Integer>(insertedReviewNo, HttpStatus.CREATED);
    }

    /**
     * 리뷰 게시글 수정
     */
    @PutMapping("/update/{review_no}")
    @ApiOperation(value = "리뷰 게시글 수정")
    public ResponseEntity<Integer> updateReview(@PathVariable int review_no, @RequestBody ReviewDto.ReviewUpdateRequestDto reviewUpdateRequestDto, HttpServletRequest req) {
        int userNo = getUserNo(req);
        int updatedReviewNo = reviewService.updateReview(review_no, reviewUpdateRequestDto, userNo);
        if(updatedReviewNo < 0) {
            return new ResponseEntity<Integer>(updatedReviewNo, HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<Integer>(updatedReviewNo, HttpStatus.CREATED);
    }

    public int getUserNo(HttpServletRequest req) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(jwtService.get(req.getHeader("auth-token")));
        return (int) ((Map<String, Object>) resultMap.get("user")).get("userNo");
    }
}