#version 330

layout (location=0) in vec3 vPosition;
layout (location=1) in vec3 vNormal;
layout (location=2) in vec2 vTexCoord;

uniform mat4 mvMatrix;
uniform mat4 pMatrix;

out vec2 texCoord;
out vec3 trNormal;
out vec4 position;

void main() {
    position = mvMatrix * vec4(vPosition, 1.0);
    gl_Position = pMatrix * position;
    texCoord = vTexCoord;
    trNormal =  normalize(mvMatrix * vec4(vNormal, 0.0)).xyz;
}