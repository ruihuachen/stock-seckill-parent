

-- 限流key 1s -> 1个
local key = KEYS[1]

-- 限流大小
local limit = tonumber(ARGV[1])

local currentLimit = tonumber(redis.call('get', key) or "0")

-- 是否超出限流
if currentLimit + 1 > limit then
    return 0;
else
    redis.call("incrby", key, 1)
    redis.call("expire", key, 2)
    return currentLimit + 1
end