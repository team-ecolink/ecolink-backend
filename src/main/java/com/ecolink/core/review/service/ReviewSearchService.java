package com.ecolink.core.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecolink.core.like.service.ReviewLikeService;
import com.ecolink.core.review.domain.Review;
import com.ecolink.core.review.dto.response.GetReviewResponse;
import com.ecolink.core.store.domain.Store;
import com.ecolink.core.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewSearchService {

	private final StoreService storeService;
	private final ReviewService reviewService;
	private final ReviewLikeService reviewLikeService;

	public Page<GetReviewResponse> getByStore(Long storeId, Pageable pageable, Long avatarId) {
		Store store = storeService.getById(storeId);

		if (avatarId != null) {
			Page<GetReviewResponse> reviewDto = reviewService.getByStore(store.getId(), pageable)
				.map(review -> GetReviewResponse.of(review, isWriter(review, avatarId)));
			return reviewLikeService.findReviewLike(reviewDto, avatarId);
		}
		return reviewService.getByStore(store.getId(), pageable)
			.map(review -> GetReviewResponse.of(review, false));
	}

	public Page<GetReviewResponse> getByStoreAndAvatar(Long storeId, Pageable pageable, Long avatarId) {
		Store store = storeService.getById(storeId);
		Page<GetReviewResponse> reviewDto = reviewService.getByStoreAndAvatar(store.getId(), avatarId, pageable)
			.map(review -> GetReviewResponse.of(review, true));
		return reviewLikeService.findReviewLike(reviewDto, avatarId);
	}

	private Boolean isWriter(Review review, Long avatarId) {
		return review.getWriter().getId().equals(avatarId);
	}

}