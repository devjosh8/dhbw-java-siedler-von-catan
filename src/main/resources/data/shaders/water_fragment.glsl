#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_textureWater;
uniform sampler2D u_textureOffset;
uniform sampler2D u_normalTexture;

uniform vec3 u_lightDirection; // Muss normalisiert sein
uniform vec3 u_lightColor;
uniform vec3 u_ambientColor;

varying vec2 v_texCoord;
varying float v_time;
varying vec3 v_normal;

void main() {
    vec2 offset_scale = vec2(1.0, 2.0);
    vec2 tiled_factor = vec2(5.0, 5.0);
    float wave_amplitude = 1.0;
    float texture_based_offset_impact = 0.03;

    vec2 tiled_uvs = v_texCoord * tiled_factor;
    tiled_uvs.y *= 0.5;


    // Wellen Berechnen in Abhängigkeit von der Zeit
    vec2 waves_offset;
    waves_offset.x = cos(v_time * 1.2 + (tiled_uvs.x + tiled_uvs.y) * offset_scale.x) * 0.04;
    waves_offset.y = sin(v_time + (tiled_uvs.x + tiled_uvs.y) * offset_scale.y) * 0.02;

    waves_offset *= wave_amplitude;

    vec2 texture_based_offset_uv = v_texCoord;
    texture_based_offset_uv.x += v_time / 40.0;
    texture_based_offset_uv.y += v_time / 32.0;

    vec2 texture_based_offset = texture2D(u_textureOffset, texture_based_offset_uv).rg;
    texture_based_offset = texture_based_offset_impact * texture_based_offset * 2.0 - 1.0;

    vec2 final_uv = v_texCoord + texture_based_offset + waves_offset; // finaler UV Offset

    // Normale aus der normal Map
    vec3 sampledNormal = texture2D(u_normalTexture, final_uv).rgb;
    sampledNormal = normalize(sampledNormal * 2.0 - 1.0); // [0,1] -> [-1,1]

    // Beleuchtung
    vec3 lightDir = normalize(u_lightDirection); // sollte aus Java kommen
    float diff = max(dot(sampledNormal, lightDir), 0.0);

    vec3 ambient = u_ambientColor;
    vec3 diffuse = diff * u_lightColor;

    vec3 lighting = ambient + diffuse;

    vec4 baseColor = texture2D(u_textureWater, final_uv);
    gl_FragColor = vec4(baseColor.rgb * lighting, baseColor.a);
}
