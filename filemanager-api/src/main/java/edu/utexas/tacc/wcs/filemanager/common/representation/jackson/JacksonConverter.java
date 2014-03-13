/**
 * Copyright (c) 2014, Texas Advanced Computing Center
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the University of Texas at Austin nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package edu.utexas.tacc.wcs.filemanager.common.representation.jackson;

import java.io.IOException;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;

/**
 * Converter between the JSON and Representation classes based on Jackson.
 * 
 * @author Jerome Louvel
 */
public class JacksonConverter extends ConverterHelper {

    private static final VariantInfo VARIANT_JSON = new VariantInfo(
            MediaType.APPLICATION_JSON);

    /**
     * Creates the marshaling {@link JacksonRepresentation}.
     * 
     * @param <T>
     * @param mediaType
     *            The target media type.
     * @param source
     *            The source object to marshal.
     * @return The marshaling {@link JacksonRepresentation}.
     */
    protected <T> JacksonRepresentation<T> create(MediaType mediaType, T source) {
        return new JacksonRepresentation<T>(mediaType, source);
    }

    /**
     * Creates the unmarshaling {@link JacksonRepresentation}.
     * 
     * @param <T>
     * @param source
     *            The source representation to unmarshal.
     * @param objectClass
     *            The object class to instantiate.
     * @return The unmarshaling {@link JacksonRepresentation}.
     */
    protected <T> JacksonRepresentation<T> create(Representation source,
            Class<T> objectClass) {
        return new JacksonRepresentation<T>(source, objectClass);
    }

    @Override
    public List<Class<?>> getObjectClasses(Variant source) {
        List<Class<?>> result = null;

        if (VARIANT_JSON.isCompatible(source)) {
            result = addObjectClass(result, Object.class);
            result = addObjectClass(result, JacksonRepresentation.class);
        }

        return result;
    }

    @Override
    public List<VariantInfo> getVariants(Class<?> source) {
        List<VariantInfo> result = null;

        if (source != null) {
            result = addVariant(result, VARIANT_JSON);
        }

        return result;
    }

    @Override
    public float score(Object source, Variant target, Resource resource) {
        float result = -1.0F;

        if (source instanceof JacksonRepresentation<?>) {
            result = 1.0F;
        } else {
            if (target == null) {
                result = 0.5F;
            } else if (VARIANT_JSON.isCompatible(target)) {
                result = 0.8F;
            } else {
                result = 0.5F;
            }
        }

        return result;
    }

    @Override
    public <T> float score(Representation source, Class<T> target,
            Resource resource) {
        float result = -1.0F;

        if (source instanceof JacksonRepresentation<?>) {
            result = 1.0F;
        } else if ((target != null)
                && JacksonRepresentation.class.isAssignableFrom(target)) {
            result = 1.0F;
        } else if (VARIANT_JSON.isCompatible(source)) {
            result = 0.8F;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T toObject(Representation source, Class<T> target,
            Resource resource) throws IOException {
        Object result = null;

        // The source for the Jackson conversion
        JacksonRepresentation<?> jacksonSource = null;

        if (source instanceof JacksonRepresentation) {
            jacksonSource = (JacksonRepresentation<?>) source;
        } else if (VARIANT_JSON.isCompatible(source)) {
            jacksonSource = create(source, target);
        }

        if (jacksonSource != null) {
            // Handle the conversion
            if ((target != null)
                    && JacksonRepresentation.class.isAssignableFrom(target)) {
                result = jacksonSource;
            } else {
                result = jacksonSource.getObject();
            }
        }

        return (T) result;
    }

    @Override
    public Representation toRepresentation(Object source, Variant target,
           Resource resource) {
        Representation result = null;

        if (source instanceof JacksonRepresentation) {
            result = (JacksonRepresentation<?>) source;
        } else {
            if (target.getMediaType() == null) {
                target.setMediaType(MediaType.APPLICATION_JSON);
            }

            if (VARIANT_JSON.isCompatible(target)) {
                JacksonRepresentation<Object> jacksonRepresentation = create(
                        target.getMediaType(), source);
                result = jacksonRepresentation;
            }
        }

        return result;
    }

    @Override
    public <T> void updatePreferences(List<Preference<MediaType>> preferences,
            Class<T> entity) {
        updatePreferences(preferences, MediaType.APPLICATION_JSON, 1.0F);
    }

}
