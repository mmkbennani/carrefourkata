package com.carrefour.domain.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Context connexion
 *
 * @author AAITIKKENE
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Context {

	private UUID correlationId;
	private String internalURL;

}
