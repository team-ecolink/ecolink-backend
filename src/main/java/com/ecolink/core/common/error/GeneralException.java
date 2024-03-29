package com.ecolink.core.common.error;

import lombok.Getter;

@Getter
public abstract class GeneralException extends RuntimeException {

	private final ErrorCode errorCode;

	public GeneralException(ErrorCode errorCode, String message) {
		super(errorCode.getMessage() +  ": " + message);
		this.errorCode = errorCode;
	}

	public GeneralException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

}

