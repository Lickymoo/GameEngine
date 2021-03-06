#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 color;
uniform int useColor;


void main(){
    if ( useColor == 1 ){
        fragColor = color;
    }else{
        fragColor = texture(texture_sampler, outTexCoord) * color;
    }
}