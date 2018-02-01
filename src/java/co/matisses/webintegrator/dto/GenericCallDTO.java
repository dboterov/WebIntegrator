package co.matisses.webintegrator.dto;

/**
 *
 * @author dbotero
 */
public class GenericCallDTO {

    private Long id;
    private Integer idObject;
    private Integer idOperation;
    private String className;
    private String methodName;
    private String parameters;
    private String returnType;
    private String xmlDataType;

    public GenericCallDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdObject() {
        return idObject;
    }

    public void setIdObject(Integer idObject) {
        this.idObject = idObject;
    }

    public Integer getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Integer idOperation) {
        this.idOperation = idOperation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getXmlDataType() {
        return xmlDataType;
    }

    public void setXmlDataType(String xmlDataType) {
        this.xmlDataType = xmlDataType;
    }
}
