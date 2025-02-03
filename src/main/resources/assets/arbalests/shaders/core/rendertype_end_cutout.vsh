#version 150

#moj_import <minecraft:projection.glsl>

in vec2 UV0;
in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 TextureMat;
uniform mat4 ProjMat;

out vec4 texProj0;
out vec2 texCord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texProj0 = projection_from_position(gl_Position);
    texCord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
}