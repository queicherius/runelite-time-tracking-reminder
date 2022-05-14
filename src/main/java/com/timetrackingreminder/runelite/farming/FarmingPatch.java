// THIS FILE WAS AUTOMATICALLY GENERATED. DO NOT EDIT IT MANUALLY. SEE README.

/*
 * Copyright (c) 2018 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.timetrackingreminder.runelite.farming;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runelite.api.annotations.Varbit;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;

@RequiredArgsConstructor(
	access = AccessLevel.PACKAGE
)
@Getter
@ToString(onlyExplicitlyIncluded = true)
class FarmingPatch
{
	@Setter(AccessLevel.PACKAGE)
	@ToString.Include
	private FarmingRegion region;
	@ToString.Include
	private final String name;
	@Getter(onMethod_ = {@Varbit})
	private final int varbit;
	@ToString.Include
	private final PatchImplementation implementation;

	String configKey()
	{
		return region.getRegionID() + "." + varbit;
	}

	String notifyConfigKey()
	{
		return TimeTrackingConfig.NOTIFY + "." + region.getRegionID() + "." + varbit;
	}
}