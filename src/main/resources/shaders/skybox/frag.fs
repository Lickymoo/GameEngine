#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;

void main(){
    fragColor = vec4(ambientLight, 1) * texture(texture_sampler, outTexCoord);
}