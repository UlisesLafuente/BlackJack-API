package com.Ulises.BlackJackAPI.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * R2DBC configuration for reactive database access.
 * Enables automatic auditing capabilities for entities.
 *
 * @author Ulises Lafuente
 */
@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {
}