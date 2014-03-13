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
import java.io.Writer;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Representation based on the Jackson library. It can serialize and deserialize
 * automatically in JSON.
 * 
 * @see <a href="http://jackson.codehaus.org/">Jackson project</a>
 * @author Jerome Louvel
 * @param <T>
 *            The type to wrap.
 */
public class JacksonXmlRepresentation<T> extends WriterRepresentation {

    /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;

    /** The JSON representation to parse. */
    private Representation jsonRepresentation;

    /** The modifiable Jackson object mapper. */
    private ObjectMapper objectMapper;

    /**
     * Constructor.
     * 
     * @param mediaType
     *            The target media type.
     * @param object
     *            The object to format.
     */
    @SuppressWarnings("unchecked")
    public JacksonXmlRepresentation(MediaType mediaType, T object) {
        super(MediaType.TEXT_XML);
        this.object = object;
        this.objectClass = (Class<T>) ((object == null) ? null : object
                .getClass());
        this.jsonRepresentation = null;
        this.objectMapper = null;
    }

    /**
     * Constructor.
     * 
     * @param representation
     *            The representation to parse.
     */
    public JacksonXmlRepresentation(Representation representation,
            Class<T> objectClass) {
        super(representation.getMediaType());
        this.object = null;
        this.objectClass = objectClass;
        this.jsonRepresentation = representation;
        this.objectMapper = null;
    }

    /**
     * Constructor.
     * 
     * @param object
     *            The object to format.
     */
    public JacksonXmlRepresentation(T object) {
        this(MediaType.TEXT_XML, object);
    }

    /**
     * Creates a Jackson object mapper based on a media type. By default, it
     * calls {@link ObjectMapper#ObjectMapper()}.
     * 
     * @return The Jackson object mapper.
     */
    protected ObjectMapper createObjectMapper() {
    	ObjectMapper mapper = null;
    	JsonFactory factory = new XmlFactory();
		factory.configure(Feature.AUTO_CLOSE_TARGET, false);
    
		mapper = new XmlMapper((XmlFactory)factory);
		mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
		return mapper;
    }

    /**
     * Returns the wrapped object, deserializing the representation with Jackson
     * if necessary.
     * 
     * @return The wrapped object.
     */
    public T getObject() {
        T result = null;

        if (this.object != null) {
            result = this.object;
        } else if (this.jsonRepresentation != null) {
            try {
                result = getObjectMapper().readValue(
                        this.jsonRepresentation.getStream(), this.objectClass);
            } catch (IOException e) {
                Context.getCurrentLogger().log(Level.WARNING,
                        "Unable to parse the object with Jackson.", e);
            }
        }

        return result;
    }

    /**
     * Returns the object class to instantiate.
     * 
     * @return The object class to instantiate.
     */
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * Returns the modifiable Jackson object mapper. Useful to customize
     * mappings.
     * 
     * @return The modifiable Jackson object mapper.
     */
    public ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = createObjectMapper();
        }

        return this.objectMapper;
    }

    /**
     * Sets the object to format.
     * 
     * @param object
     *            The object to format.
     */
    public void setObject(T object) {
        this.object = object;
    }

    /**
     *Sets the object class to instantiate.
     * 
     * @param objectClass
     *            The object class to instantiate.
     */
    public void setObjectClass(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * Sets the Jackson object mapper.
     * 
     * @param objectMapper
     *            The Jackson object mapper.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void write(Writer writer) throws IOException {
        if (jsonRepresentation != null) {
            jsonRepresentation.write(writer);
        } else if (object != null) {
            getObjectMapper().writeValue(writer, object);
        }
    }
}
