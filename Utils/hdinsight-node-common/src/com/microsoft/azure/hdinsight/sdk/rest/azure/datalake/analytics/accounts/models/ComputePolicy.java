/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.hdinsight.sdk.rest.azure.datalake.analytics.accounts.models;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.rest.serializer.JsonFlatten;
import com.microsoft.azure.SubResource;

/**
 * Data Lake Analytics compute policy information.
 */
@JsonFlatten
public class ComputePolicy extends SubResource {
    /**
     * The AAD object identifier for the entity to create a policy for.
     */
    @JsonProperty(value = "properties.objectId", access = JsonProperty.Access.WRITE_ONLY)
    private UUID objectId;

    /**
     * The type of AAD object the object identifier refers to. Possible values
     * include: 'User', 'Group', 'ServicePrincipal'.
     */
    @JsonProperty(value = "properties.objectType", access = JsonProperty.Access.WRITE_ONLY)
    private AADObjectType objectType;

    /**
     * The maximum degree of parallelism per job this user can use to submit
     * jobs.
     */
    @JsonProperty(value = "properties.maxDegreeOfParallelismPerJob", access = JsonProperty.Access.WRITE_ONLY)
    private Integer maxDegreeOfParallelismPerJob;

    /**
     * The minimum priority per job this user can use to submit jobs.
     */
    @JsonProperty(value = "properties.minPriorityPerJob", access = JsonProperty.Access.WRITE_ONLY)
    private Integer minPriorityPerJob;

    /**
     * The resource name.
     */
    @JsonProperty(value = "name", access = JsonProperty.Access.WRITE_ONLY)
    private String name;

    /**
     * The resource type.
     */
    @JsonProperty(value = "type", access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    /**
     * Get the objectId value.
     *
     * @return the objectId value
     */
    public UUID objectId() {
        return this.objectId;
    }

    /**
     * Get the objectType value.
     *
     * @return the objectType value
     */
    public AADObjectType objectType() {
        return this.objectType;
    }

    /**
     * Get the maxDegreeOfParallelismPerJob value.
     *
     * @return the maxDegreeOfParallelismPerJob value
     */
    public Integer maxDegreeOfParallelismPerJob() {
        return this.maxDegreeOfParallelismPerJob;
    }

    /**
     * Get the minPriorityPerJob value.
     *
     * @return the minPriorityPerJob value
     */
    public Integer minPriorityPerJob() {
        return this.minPriorityPerJob;
    }

    /**
     * Get the name value.
     *
     * @return the name value
     */
    public String name() {
        return this.name;
    }

    /**
     * Get the type value.
     *
     * @return the type value
     */
    public String type() {
        return this.type;
    }

}