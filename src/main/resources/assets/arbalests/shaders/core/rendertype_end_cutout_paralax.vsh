#version 150

#moj_import <minecraft:projection.glsl>

in vec2 UV0;
in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 TextureMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform vec3 CameraPos;

out vec2 texCord0;
out vec3 view;
out vec3 uv3d;

vec4 ModelPos = vec4(Position, 1.0);
mat4 ICamJiggleMat = mat4(inverse(mat3(ProjMat))) * ProjMat;

void main() {

    // Strip the Z translation out
    ICamJiggleMat[2].w = 0.0;
    ICamJiggleMat[3].z = 0.0;

    gl_Position = ProjMat * ModelPos;
    view = IViewRotMat * (ICamJiggleMat * ModelPos).xyz;
    uv3d.st = (Position).xz; //(IViewRotMat * Position).xz;
    uv3d.st += (ModelViewMat * vec4(CameraPos, 1.0)).xz;
    uv3d.z = UV0.y;

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
}