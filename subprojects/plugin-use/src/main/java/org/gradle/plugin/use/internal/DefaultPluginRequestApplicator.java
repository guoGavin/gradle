/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.plugin.use.internal;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.plugins.UnknownPluginException;
import org.gradle.internal.exceptions.LocationAwareException;
import org.gradle.plugin.use.resolve.internal.PluginResolution;
import org.gradle.plugin.use.resolve.internal.PluginResolver;

public class DefaultPluginRequestApplicator implements PluginRequestApplicator {

    private final PluginResolver pluginResolver;
    private final Action<? super PluginResolution> pluginResolutionHandler;

    public DefaultPluginRequestApplicator(PluginResolver pluginResolver, Action<? super PluginResolution> pluginResolutionHandler) {
        this.pluginResolver = pluginResolver;
        this.pluginResolutionHandler = pluginResolutionHandler;
    }

    public void applyPlugin(PluginRequest request) {
        PluginResolution resolution;
        try {
            resolution = pluginResolver.resolve(request);
        } catch (Exception e) {
            throw new LocationAwareException(
                    new GradleException(String.format("Error resolving plugin %s.", request.getDisplayName()), e),
                    request.getScriptSource(), request.getLineNumber());
        }
        if (resolution == null) {
            throw new LocationAwareException(
                    new UnknownPluginException(String.format("Plugin %s not found in %s", request.getDisplayName(), pluginResolver.getDescriptionForNotFoundMessage())),
                    request.getScriptSource(),
                    request.getLineNumber()
            );
        }

        pluginResolutionHandler.execute(resolution);
    }
}
