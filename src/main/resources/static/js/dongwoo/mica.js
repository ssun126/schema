/*!
 * ----------------------------------------------------------------------
 * Mica: Front-end site library
 * @license MIT
 * Other scripts may access this api using an async handler:
 *   var Mica = Mica || [];
 *   Mica.push(readyFunction);
 * ----------------------------------------------------------------------
 */

var Mica = { w: Mica };
Mica.init = function () {
    //'use strict';
    //if (typeof authCheck != "undefined") {
    //    authCheck();
    //}
    var $ = window.$;
    var api = {};
    var modules = {};
    var primary = [];
    var secondary = this.w || [];
    var $win = $(window);
    var $doc = $(document);
    var _ = api._ = underscore();
    var domready = false;
    var tram = window.tram;
    var Modernizr = window.Modernizr;
    var noop = function () { };
    tram.config.hideBackface = false;
    tram.config.keepInherited = true;
    var lang = $('html').attr('lang') || $('html').attr('data-language');
    lang = lscache.get("language") || lang;
    //    var lang = getCookie("data-language") || $('html').attr('lang') || $('html').attr('data-language');

    /**
     * Mica.define() - Define a mica.js module
     * @param  {string} name
     * @param  {function} factory
     */
    //api.agentURL = "http://112.216.110.174:8008" || "";
    api.agentURL = "";

    api.define = function (name, factory) {
        var module = modules[name] = factory($, _);
        if (!module) return;
        // If running in Mica app, subscribe to design/preview events
        if (api.env()) {
            $.isFunction(module.design) && window.addEventListener('__wf_design', module.design);
            $.isFunction(module.preview) && window.addEventListener('__wf_preview', module.preview);
        }
        // Subscribe to module front-end events
        $.isFunction(module.destroy) && $win.on('__wf_destroy', module.destroy);
        // Look for a ready method on module
        if (module.ready && $.isFunction(module.ready)) {
            // If domready has already happened, call ready method
            if (domready) module.ready();
                // Otherwise push ready method into primary queue
            else primary.push(module.ready);
        }
    };

    /**
     * Mica.require() - Load a Mica.js module
     * @param  {string} name
     * @return {object}
     */
    api.require = function (name) {
        return modules[name];
    };

    /**
     * Mica.push() - Add a ready handler into secondary queue
     * @param {function} ready  Callback to invoke on domready
     */
    api.push = function (ready) {
        // If domready has already happened, invoke handler
        if (domready) {
            $.isFunction(ready) && ready();
            return;
        }
        // Otherwise push into secondary queue
        secondary.push(ready);
    };

    /**
     * Mica.env() - Get the state of the Mica app
     * @param {string} mode [optional]
     * @return {boolean}
     */
    api.env = function (mode) {
        var designFlag = window.__wf_design;
        var inApp = typeof designFlag != 'undefined';
        if (!mode) return inApp;
        if (mode == 'design') return inApp && designFlag;
        if (mode == 'preview') return inApp && !designFlag;
        if (mode == 'slug') return inApp && window.__wf_slug;
    };

    api.lang = function () {
        var lang2 = $('html').attr('lang') || $('html').attr('data-language');
        lang2 = lscache.get("language") || lang;
        return lang2;
        //return lang;
    };

    // Feature detects + browser sniffs  ಠ_ಠ
    var userAgent = navigator.userAgent.toLowerCase();
    var appVersion = navigator.appVersion.toLowerCase();
    var touch = api.env.touch = ('ontouchstart' in window) || window.DocumentTouch && document instanceof window.DocumentTouch;
    var chrome = api.env.chrome = /chrome/.test(userAgent) && /Google/.test(navigator.vendor) && parseInt(appVersion.match(/chrome\/(\d+)\./)[1], 10);
    var ios = api.env.ios = Modernizr && Modernizr.ios;
    api.env.safari = /safari/.test(userAgent) && !chrome && !ios;

    // Maintain current touch target to prevent late clicks on touch devices
    var touchTarget;
    // Listen for both events to support touch/mouse hybrid devices
    touch && $doc.on('touchstart mousedown', function (evt) {
        touchTarget = evt.target;
    });

    /**
     * Mica.validClick() - validate click target against current touch target
     * @param  {HTMLElement} clickTarget  Element being clicked
     * @return {Boolean}  True if click target is valid (always true on non-touch)
     */
    api.validClick = touch ? function (clickTarget) {
        return clickTarget === touchTarget || $.contains(clickTarget, touchTarget);
    } : function () { return true; };

    /**
     * Mica.resize, Mica.scroll - throttled event proxies
     */
    var resizeEvents = 'resize.mica orientationchange.mica load.mica';
    var scrollEvents = 'scroll.mica ' + resizeEvents;
    api.resize = eventProxy($win, resizeEvents);
    api.scroll = eventProxy($win, scrollEvents);
    api.redraw = eventProxy();

    // Create a proxy instance for throttled events
    function eventProxy(target, types) {

        // Set up throttled method (using custom frame-based _.throttle)
        var handlers = [];
        var proxy = {};
        proxy.up = _.throttle(function (evt) {
            _.each(handlers, function (h) { h(evt); });
        });

        // Bind events to target
        if (target && types) target.on(types, proxy.up);

        /**
         * Add an event handler
         * @param  {function} handler
         */
        proxy.on = function (handler) {
            if (typeof handler != 'function') return;
            if (_.contains(handlers, handler)) return;
            handlers.push(handler);
        };

        /**
         * Remove an event handler
         * @param  {function} handler
         */
        proxy.off = function (handler) {
            handlers = _.filter(handlers, function (h) {
                return h !== handler;
            });
        };
        return proxy;
    }

    // Provide optional IX events to components
    api.ixEvents = function () {
        var ix = api.require('ix');
        return (ix && ix.events) || {
            reset: noop,
            intro: noop,
            outro: noop
        };
    };

    // Mica.location() - Wrap window.location in api
    api.location = function (url) {
        window.location = url;
    };

    // Mica.app - Designer-specific methods
    api.app = api.env() ? {} : null;
    if (api.app) {

        // Trigger redraw for specific elements
        var Event = window.Event;
        var redraw = new Event('__wf_redraw');
        api.app.redrawElement = function (i, el) { el.dispatchEvent(redraw); };

        // Mica.location - Re-route location change to trigger an event
        api.location = function (url) {
            window.dispatchEvent(new CustomEvent('__wf_location', { detail: url }));
        };
    }

    // Mica.ready() - Call primary and secondary handlers
    api.ready = function () {
        domready = true;
        $.each(primary.concat(secondary), function (index, value) {
            $.isFunction(value) && value();
        });
        // Trigger resize
        api.resize.up();

        lscache.flushExpired();// remove only expired items

        //$('body').append("<div id='jqxNotification'><div>Notice: <span id='jqxNotice' style='font-weight: bold;'></span></div></div>");
        //$("#jqxNotification").jqxNotification({
        //    width: "auto", position: "top-right", opacity: 0.9,
        //    autoOpen: false, closeOnClick: true, autoClose: true, template: "info", blink: false
        //});

        $('a.btn, a.button, a[name="popup-btn"]').on('click', function (event) {
            var popupDIV = $(this).attr('target') == "popup" ? $(this).attr('href') : null;
            if (popupDIV) {
                event.preventDefault();
                window.location.hash = "";
                var width = $(popupDIV).width() || 400;
                width > 1000 && (width = 1000);
                var height = $(popupDIV).height() || 100;

                var css = {
                    left: ($(window).width() - width) / 2 + 'px',
                    width: width + 'px',
                    'min-width': width + 'px',
                    'min-height': height + 'px',
                };

                $.getScript('/Content/theme/js/plugins/popup/jquery.blockUI.js', function () {
                    $.blockUI({ message: $(popupDIV), css: css, onOverlayClick: $.unblockUI });
                    //$(".blockMsg").draggable().resizable();
                    $(".blockMsg").draggable();;
                    $(window).trigger('resize');
                });
            }
            //다른 곳에서 입력한 이벤트를 타기전에 여기서 return으로 끝내기 때문에 주석 처리함
            //return false;
        });

    };

    /**
     * Mica.load() - Add a window load handler that will run even if load event has already happened
     * @param  {function} handler
     */
    var deferLoad;
    api.load = function (handler) {
        deferLoad.then(handler);
    };

    function bindLoad() {
        // Reject any previous deferred (to support destroy)
        if (deferLoad) {
            deferLoad.reject();
            $win.off('load', deferLoad.resolve);
        }
        // Create deferred and bind window load event
        deferLoad = new $.Deferred();
        $win.on('load', deferLoad.resolve);
    }

    // Mica.destroy() - Trigger a cleanup event for all modules
    api.destroy = function () {
        $win.triggerHandler('__wf_destroy');
        // If load event has not yet fired, replace the deferred
        if (deferLoad.state() == 'pending') bindLoad();
    };

    // Listen for domready
    $(api.ready);

    // Listen for window.onload and resolve deferred
    bindLoad();

    /*!
     * Mica._ (aka) Underscore.js 1.6.0 (custom build)
     * _.each
     * _.map
     * _.find
     * _.filter
     * _.any
     * _.contains
     * _.delay
     * _.defer
     * _.throttle (mica)
     * _.debounce
     * _.keys
     * _.has
     * _.now
     *
     * http://underscorejs.org
     * (c) 2009-2013 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
     * Underscore may be freely distributed under the MIT license.
     */
    function underscore() {
        var _ = {};

        // Current version.
        _.VERSION = '1.6.0-Mica';

        // Establish the object that gets returned to break out of a loop iteration.
        var breaker = {};

        // Save bytes in the minified (but not gzipped) version:
        var ArrayProto = Array.prototype, ObjProto = Object.prototype, FuncProto = Function.prototype;

        // Create quick reference variables for speed access to core prototypes.
        var
          push = ArrayProto.push,
          slice = ArrayProto.slice,
          concat = ArrayProto.concat,
          toString = ObjProto.toString,
          hasOwnProperty = ObjProto.hasOwnProperty;

        // All **ECMAScript 5** native function implementations that we hope to use
        // are declared here.
        var
          nativeForEach = ArrayProto.forEach,
          nativeMap = ArrayProto.map,
          nativeReduce = ArrayProto.reduce,
          nativeReduceRight = ArrayProto.reduceRight,
          nativeFilter = ArrayProto.filter,
          nativeEvery = ArrayProto.every,
          nativeSome = ArrayProto.some,
          nativeIndexOf = ArrayProto.indexOf,
          nativeLastIndexOf = ArrayProto.lastIndexOf,
          nativeIsArray = Array.isArray,
          nativeKeys = Object.keys,
          nativeBind = FuncProto.bind;

        // Collection Functions
        // --------------------

        // The cornerstone, an `each` implementation, aka `forEach`.
        // Handles objects with the built-in `forEach`, arrays, and raw objects.
        // Delegates to **ECMAScript 5**'s native `forEach` if available.
        var each = _.each = _.forEach = function (obj, iterator, context) {
            /* jshint shadow:true */
            if (obj == null) return obj;
            if (nativeForEach && obj.forEach === nativeForEach) {
                obj.forEach(iterator, context);
            } else if (obj.length === +obj.length) {
                for (var i = 0, length = obj.length; i < length; i++) {
                    if (iterator.call(context, obj[i], i, obj) === breaker) return;
                }
            } else {
                var keys = _.keys(obj);
                for (var i = 0, length = keys.length; i < length; i++) {
                    if (iterator.call(context, obj[keys[i]], keys[i], obj) === breaker) return;
                }
            }
            return obj;
        };

        // Return the results of applying the iterator to each element.
        // Delegates to **ECMAScript 5**'s native `map` if available.
        _.map = _.collect = function (obj, iterator, context) {
            var results = [];
            if (obj == null) return results;
            if (nativeMap && obj.map === nativeMap) return obj.map(iterator, context);
            each(obj, function (value, index, list) {
                results.push(iterator.call(context, value, index, list));
            });
            return results;
        };

        // Return the first value which passes a truth test. Aliased as `detect`.
        _.find = _.detect = function (obj, predicate, context) {
            var result;
            any(obj, function (value, index, list) {
                if (predicate.call(context, value, index, list)) {
                    result = value;
                    return true;
                }
            });
            return result;
        };

        // Return all the elements that pass a truth test.
        // Delegates to **ECMAScript 5**'s native `filter` if available.
        // Aliased as `select`.
        _.filter = _.select = function (obj, predicate, context) {
            var results = [];
            if (obj == null) return results;
            if (nativeFilter && obj.filter === nativeFilter) return obj.filter(predicate, context);
            each(obj, function (value, index, list) {
                if (predicate.call(context, value, index, list)) results.push(value);
            });
            return results;
        };

        // Determine if at least one element in the object matches a truth test.
        // Delegates to **ECMAScript 5**'s native `some` if available.
        // Aliased as `any`.
        var any = _.some = _.any = function (obj, predicate, context) {
            predicate || (predicate = _.identity);
            var result = false;
            if (obj == null) return result;
            if (nativeSome && obj.some === nativeSome) return obj.some(predicate, context);
            each(obj, function (value, index, list) {
                if (result || (result = predicate.call(context, value, index, list))) return breaker;
            });
            return !!result;
        };

        // Determine if the array or object contains a given value (using `===`).
        // Aliased as `include`.
        _.contains = _.include = function (obj, target) {
            if (obj == null) return false;
            if (nativeIndexOf && obj.indexOf === nativeIndexOf) return obj.indexOf(target) != -1;
            return any(obj, function (value) {
                return value === target;
            });
        };

        // Function (ahem) Functions
        // --------------------

        // Delays a function for the given number of milliseconds, and then calls
        // it with the arguments supplied.
        _.delay = function (func, wait) {
            var args = slice.call(arguments, 2);
            return setTimeout(function () { return func.apply(null, args); }, wait);
        };

        // Defers a function, scheduling it to run after the current call stack has
        // cleared.
        _.defer = function (func) {
            return _.delay.apply(_, [func, 1].concat(slice.call(arguments, 1)));
        };

        // Returns a function, that, when invoked, will only be triggered once every
        // browser animation frame - using tram's requestAnimationFrame polyfill.
        _.throttle = function (func) {
            var wait, args, context;
            return function () {
                if (wait) return;
                wait = true;
                args = arguments;
                context = this;
                tram.frame(function () {
                    wait = false;
                    func.apply(context, args);
                });
            };
        };

        // Returns a function, that, as long as it continues to be invoked, will not
        // be triggered. The function will be called after it stops being called for
        // N milliseconds. If `immediate` is passed, trigger the function on the
        // leading edge, instead of the trailing.
        _.debounce = function (func, wait, immediate) {
            var timeout, args, context, timestamp, result;

            var later = function () {
                var last = _.now() - timestamp;
                if (last < wait) {
                    timeout = setTimeout(later, wait - last);
                } else {
                    timeout = null;
                    if (!immediate) {
                        result = func.apply(context, args);
                        context = args = null;
                    }
                }
            };

            return function () {
                context = this;
                args = arguments;
                timestamp = _.now();
                var callNow = immediate && !timeout;
                if (!timeout) {
                    timeout = setTimeout(later, wait);
                }
                if (callNow) {
                    result = func.apply(context, args);
                    context = args = null;
                }

                return result;
            };
        };

        // Object Functions
        // ----------------

        // Retrieve the names of an object's properties.
        // Delegates to **ECMAScript 5**'s native `Object.keys`
        _.keys = function (obj) {
            if (!_.isObject(obj)) return [];
            if (nativeKeys) return nativeKeys(obj);
            var keys = [];
            for (var key in obj) if (_.has(obj, key)) keys.push(key);
            return keys;
        };

        // Shortcut function for checking if an object has a given property directly
        // on itself (in other words, not on a prototype).
        _.has = function (obj, key) {
            return hasOwnProperty.call(obj, key);
        };

        // Is a given variable an object?
        _.isObject = function (obj) {
            return obj === Object(obj);
        };

        // Utility Functions
        // -----------------

        // A (possibly faster) way to get the current timestamp as an integer.
        _.now = Date.now || function () { return new Date().getTime(); };

        // Export underscore
        return _;
    }

    // Export api
    Mica = api;
};

function JSONtoString(object) {
    var results = [];
    for (var property in object) {
        var value = object[property];
        if (value)
            results.push(property.toString() + ': ' + value);
    }
    return '{' + results.join(', ') + '}';
};

/*!
 * ----------------------------------------------------------------------
 * Mica: 3rd party plugins
 */
/* lscache  A localStorage-based memcache-inspired client-side caching library. https://github.com/pamelafox/lscache */
!function (a, b) { "function" == typeof define && define.amd ? define([], b) : "undefined" != typeof module && module.exports ? module.exports = b() : a.lscache = b() }(this, function () { function a() { var a = "__lscachetest__", c = a; if (void 0 !== m) return m; try { g(a, c), h(a), m = !0 } catch (d) { m = b(d) ? !0 : !1 } return m } function b(a) { return a && "QUOTA_EXCEEDED_ERR" === a.name || "NS_ERROR_DOM_QUOTA_REACHED" === a.name || "QuotaExceededError" === a.name ? !0 : !1 } function c() { return void 0 === n && (n = null != window.JSON), n } function d(a) { return a + p } function e() { return Math.floor((new Date).getTime() / r) } function f(a) { return localStorage.getItem(o + t + a) } function g(a, b) { localStorage.removeItem(o + t + a), localStorage.setItem(o + t + a, b) } function h(a) { localStorage.removeItem(o + t + a) } function i(a) { for (var b = new RegExp("^" + o + t + "(.*)"), c = localStorage.length - 1; c >= 0; --c) { var e = localStorage.key(c); e = e && e.match(b), e = e && e[1], e && e.indexOf(p) < 0 && a(e, d(e)) } } function j(a) { var b = d(a); h(a), h(b) } function k(a) { var b = d(a), c = f(b); if (c) { var g = parseInt(c, q); if (e() >= g) return h(a), h(b), !0 } } function l(a, b) { u && "console" in window && "function" == typeof window.console.warn && (window.console.warn("lscache - " + a), b && window.console.warn("lscache - The error was: " + b.message)) } var m, n, o = "lscache-", p = "-cacheexpiration", q = 10, r = 6e4, s = Math.floor(864e13 / r), t = "", u = !1, v = { set: function (k, m, n) { if (a()) { if ("string" != typeof m) { if (!c()) return; try { m = JSON.stringify(m) } catch (o) { return } } try { g(k, m) } catch (o) { if (!b(o)) return void l("Could not add item with key '" + k + "'", o); var p, r = []; i(function (a, b) { var c = f(b); c = c ? parseInt(c, q) : s, r.push({ key: a, size: (f(a) || "").length, expiration: c }) }), r.sort(function (a, b) { return b.expiration - a.expiration }); for (var t = (m || "").length; r.length && t > 0;) p = r.pop(), l("Cache is full, removing item with key '" + k + "'"), j(p.key), t -= p.size; try { g(k, m) } catch (o) { return void l("Could not add item with key '" + k + "', perhaps it's too big?", o) } } n ? g(d(k), (e() + n).toString(q)) : h(d(k)) } }, get: function (b) { if (!a()) return null; if (k(b)) return null; var d = f(b); if (!d || !c()) return d; try { return JSON.parse(d) } catch (e) { return d } }, remove: function (b) { a() && j(b) }, supported: function () { return a() }, flush: function () { a() && i(function (a) { j(a) }) }, flushExpired: function () { a() && i(function (a) { k(a) }) }, setBucket: function (a) { t = a }, resetBucket: function () { t = "" }, enableWarnings: function (a) { u = a } }; return v });
//!function (a, b) {"function" == typeof define && define.amd ? define([], b): "undefined" != typeof module && module.exports ? module.exports = b(): a.lscache = b() }(this, function () {function a() { var a = "__lscachetest__", c = a; if (void 0 !== m) return m; try { g(a, c), h(a), m = !0 } catch(d) { m = b(d) ? !0 : !1 } return m } function b(a) { return a && "QUOTA_EXCEEDED_ERR" === a.name || "NS_ERROR_DOM_QUOTA_REACHED" === a.name || "QuotaExceededError" === a.name ? !0: !1 } function c() { return void 0 === n && (n = null != window.JSON), n } function d(a) { return a +p } function e() { return Math.floor((new Date).getTime() / r) } function f(a) { return sessionStorage.getItem(o +t +a) } function g(a, b) {sessionStorage.removeItem(o +t +a), sessionStorage.setItem(o +t + a, b) } function h(a) { sessionStorage.removeItem(o +t + a) } function i(a) { for (var b = new RegExp("^" +o +t + "(.*)"), c = sessionStorage.length - 1; c >= 0; --c) { var e = sessionStorage.key(c); e = e && e.match(b), e = e && e[1], e && e.indexOf(p) < 0 && a(e, d(e))}} function j(a) { var b = d(a); h(a), h(b) } function k(a) { var b = d(a), c = f(b); if(c) { var g = parseInt(c, q); if (e() >= g) return h(a), h(b), !0 } } function l(a, b) { u && "console" in window && "function" == typeof window.console.warn &&(window.console.warn("lscache - " +a), b && window.console.warn("lscache - The error was: " + b.message)) } var m, n, o = "lscache-", p = "-cacheexpiration", q = 10, r = 6e4, s = Math.floor(864e13 / r), t = "", u = !1, v = { set: function (k, m, n) { if (a()) { if ("string" != typeof m) { if (!c()) return; try { m = JSON.stringify(m) } catch(o) { return } } try { g(k, m) } catch (o) { if(!b(o)) return void l("Could not add item with key '" +k + "'", o); var p, r =[]; i(function (a, b) { var c = f(b); c = c ? parseInt(c, q) : s, r.push({ key : a, size : (f(a) || "").length, expiration: c }) }), r.sort(function (a, b) { return b.expiration -a.expiration }); for (var t = (m || "").length; r.length && t > 0;) p = r.pop(), l("Cache is full, removing item with key '" + k + "'"), j(p.key), t -= p.size; try { g(k, m) } catch (o) { return void l("Could not add item with key '" +k + "', perhaps it's too big?", o) } } n ? g(d(k), (e() + n).toString(q)): h(d(k)) } }, get: function (b) { if (!a()) return null; if (k(b)) return null; var d = f(b); if(!d || !c()) return d; try { return JSON.parse(d) } catch (e) { return d } }, remove: function (b) { a() && j(b) }, supported: function () { return a() }, flush: function () { a() && i(function (a) { j(a) }) }, flushExpired: function () { a() && i(function (a) { k(a) }) }, setBucket: function (a) { t = a }, resetBucket: function () { t = "" }, enableWarnings: function (a) { u = a } }; return v });
/* jshint ignore:start */
/*!
 * tram.js v0.8.1-global
 * Cross-browser CSS3 transitions in JavaScript
 * https://github.com/bkwld/tram
 * MIT License
 */
window.tram = function (a) {
    function b(a, b) { var c = new L.Bare; return c.init(a, b) } function c(a) { return a.replace(/[A-Z]/g, function (a) { return "-" + a.toLowerCase() }) } function d(a) { var b = parseInt(a.slice(1), 16), c = b >> 16 & 255, d = b >> 8 & 255, e = 255 & b; return [c, d, e] } function e(a, b, c) { return "#" + (1 << 24 | a << 16 | b << 8 | c).toString(16).slice(1) } function f() { } function g(a, b) { _("Type warning: Expected: [" + a + "] Got: [" + typeof b + "] " + b) } function h(a, b, c) { _("Units do not match [" + a + "]: " + b + ", " + c) } function i(a, b, c) { if (void 0 !== b && (c = b), void 0 === a) return c; var d = c; return Z.test(a) || !$.test(a) ? d = parseInt(a, 10) : $.test(a) && (d = 1e3 * parseFloat(a)), 0 > d && (d = 0), d === d ? d : c } function j(a) { for (var b = -1, c = a ? a.length : 0, d = []; ++b < c;) { var e = a[b]; e && d.push(e) } return d } var k = function (a, b, c) {
        function d(a) { return "object" == typeof a } function e(a) { return "function" == typeof a } function f() { } function g(h, i) {
            function j() {
                var a = new k;
                return e(a.init) && a.init.apply(a, arguments), a
            } function k() { } i === c && (i = h, h = Object), j.Bare = k; var l, m = f[a] = h[a], n = k[a] = j[a] = new f; return n.constructor = j, j.mixin = function (b) { return k[a] = j[a] = g(j, b)[a], j }, j.open = function (a) { if (l = {}, e(a) ? l = a.call(j, n, m, j, h) : d(a) && (l = a), d(l)) for (var c in l) b.call(l, c) && (n[c] = l[c]); return e(n.init) || (n.init = h), j }, j.open(i)
        } return g
    }("prototype", {}.hasOwnProperty), l = { ease: ["ease", function (a, b, c, d) { var e = (a /= d) * a, f = e * a; return b + c * (-2.75 * f * e + 11 * e * e + -15.5 * f + 8 * e + .25 * a) }], "ease-in": ["ease-in", function (a, b, c, d) { var e = (a /= d) * a, f = e * a; return b + c * (-1 * f * e + 3 * e * e + -3 * f + 2 * e) }], "ease-out": ["ease-out", function (a, b, c, d) { var e = (a /= d) * a, f = e * a; return b + c * (.3 * f * e + -1.6 * e * e + 2.2 * f + -1.8 * e + 1.9 * a) }], "ease-in-out": ["ease-in-out", function (a, b, c, d) { var e = (a /= d) * a, f = e * a; return b + c * (2 * f * e + -5 * e * e + 2 * f + 2 * e) }], linear: ["linear", function (a, b, c, d) { return c * a / d + b }], "ease-in-quad": ["cubic-bezier(0.550, 0.085, 0.680, 0.530)", function (a, b, c, d) { return c * (a /= d) * a + b }], "ease-out-quad": ["cubic-bezier(0.250, 0.460, 0.450, 0.940)", function (a, b, c, d) { return -c * (a /= d) * (a - 2) + b }], "ease-in-out-quad": ["cubic-bezier(0.455, 0.030, 0.515, 0.955)", function (a, b, c, d) { return (a /= d / 2) < 1 ? c / 2 * a * a + b : -c / 2 * (--a * (a - 2) - 1) + b }], "ease-in-cubic": ["cubic-bezier(0.550, 0.055, 0.675, 0.190)", function (a, b, c, d) { return c * (a /= d) * a * a + b }], "ease-out-cubic": ["cubic-bezier(0.215, 0.610, 0.355, 1)", function (a, b, c, d) { return c * ((a = a / d - 1) * a * a + 1) + b }], "ease-in-out-cubic": ["cubic-bezier(0.645, 0.045, 0.355, 1)", function (a, b, c, d) { return (a /= d / 2) < 1 ? c / 2 * a * a * a + b : c / 2 * ((a -= 2) * a * a + 2) + b }], "ease-in-quart": ["cubic-bezier(0.895, 0.030, 0.685, 0.220)", function (a, b, c, d) { return c * (a /= d) * a * a * a + b }], "ease-out-quart": ["cubic-bezier(0.165, 0.840, 0.440, 1)", function (a, b, c, d) { return -c * ((a = a / d - 1) * a * a * a - 1) + b }], "ease-in-out-quart": ["cubic-bezier(0.770, 0, 0.175, 1)", function (a, b, c, d) { return (a /= d / 2) < 1 ? c / 2 * a * a * a * a + b : -c / 2 * ((a -= 2) * a * a * a - 2) + b }], "ease-in-quint": ["cubic-bezier(0.755, 0.050, 0.855, 0.060)", function (a, b, c, d) { return c * (a /= d) * a * a * a * a + b }], "ease-out-quint": ["cubic-bezier(0.230, 1, 0.320, 1)", function (a, b, c, d) { return c * ((a = a / d - 1) * a * a * a * a + 1) + b }], "ease-in-out-quint": ["cubic-bezier(0.860, 0, 0.070, 1)", function (a, b, c, d) { return (a /= d / 2) < 1 ? c / 2 * a * a * a * a * a + b : c / 2 * ((a -= 2) * a * a * a * a + 2) + b }], "ease-in-sine": ["cubic-bezier(0.470, 0, 0.745, 0.715)", function (a, b, c, d) { return -c * Math.cos(a / d * (Math.PI / 2)) + c + b }], "ease-out-sine": ["cubic-bezier(0.390, 0.575, 0.565, 1)", function (a, b, c, d) { return c * Math.sin(a / d * (Math.PI / 2)) + b }], "ease-in-out-sine": ["cubic-bezier(0.445, 0.050, 0.550, 0.950)", function (a, b, c, d) { return -c / 2 * (Math.cos(Math.PI * a / d) - 1) + b }], "ease-in-expo": ["cubic-bezier(0.950, 0.050, 0.795, 0.035)", function (a, b, c, d) { return 0 === a ? b : c * Math.pow(2, 10 * (a / d - 1)) + b }], "ease-out-expo": ["cubic-bezier(0.190, 1, 0.220, 1)", function (a, b, c, d) { return a === d ? b + c : c * (-Math.pow(2, -10 * a / d) + 1) + b }], "ease-in-out-expo": ["cubic-bezier(1, 0, 0, 1)", function (a, b, c, d) { return 0 === a ? b : a === d ? b + c : (a /= d / 2) < 1 ? c / 2 * Math.pow(2, 10 * (a - 1)) + b : c / 2 * (-Math.pow(2, -10 * --a) + 2) + b }], "ease-in-circ": ["cubic-bezier(0.600, 0.040, 0.980, 0.335)", function (a, b, c, d) { return -c * (Math.sqrt(1 - (a /= d) * a) - 1) + b }], "ease-out-circ": ["cubic-bezier(0.075, 0.820, 0.165, 1)", function (a, b, c, d) { return c * Math.sqrt(1 - (a = a / d - 1) * a) + b }], "ease-in-out-circ": ["cubic-bezier(0.785, 0.135, 0.150, 0.860)", function (a, b, c, d) { return (a /= d / 2) < 1 ? -c / 2 * (Math.sqrt(1 - a * a) - 1) + b : c / 2 * (Math.sqrt(1 - (a -= 2) * a) + 1) + b }], "ease-in-back": ["cubic-bezier(0.600, -0.280, 0.735, 0.045)", function (a, b, c, d, e) { return void 0 === e && (e = 1.70158), c * (a /= d) * a * ((e + 1) * a - e) + b }], "ease-out-back": ["cubic-bezier(0.175, 0.885, 0.320, 1.275)", function (a, b, c, d, e) { return void 0 === e && (e = 1.70158), c * ((a = a / d - 1) * a * ((e + 1) * a + e) + 1) + b }], "ease-in-out-back": ["cubic-bezier(0.680, -0.550, 0.265, 1.550)", function (a, b, c, d, e) { return void 0 === e && (e = 1.70158), (a /= d / 2) < 1 ? c / 2 * a * a * (((e *= 1.525) + 1) * a - e) + b : c / 2 * ((a -= 2) * a * (((e *= 1.525) + 1) * a + e) + 2) + b }] }, m = { "ease-in-back": "cubic-bezier(0.600, 0, 0.735, 0.045)", "ease-out-back": "cubic-bezier(0.175, 0.885, 0.320, 1)", "ease-in-out-back": "cubic-bezier(0.680, 0, 0.265, 1)" }, n = document, o = window, p = "bkwld-tram", q = /[\-\.0-9]/g, r = /[A-Z]/, s = "number", t = /^(rgb|#)/, u = /(em|cm|mm|in|pt|pc|px)$/, v = /(em|cm|mm|in|pt|pc|px|%)$/, w = /(deg|rad|turn)$/, x = "unitless", y = /(all|none) 0s ease 0s/, z = /^(width|height)$/, A = " ", B = n.createElement("a"), C = ["Webkit", "Moz", "O", "ms"], D = ["-webkit-", "-moz-", "-o-", "-ms-"], E = function (a) { if (a in B.style) return { dom: a, css: a }; var b, c, d = "", e = a.split("-"); for (b = 0; b < e.length; b++) d += e[b].charAt(0).toUpperCase() + e[b].slice(1); for (b = 0; b < C.length; b++) if (c = C[b] + d, c in B.style) return { dom: c, css: D[b] + a } }, F = b.support = { bind: Function.prototype.bind, transform: E("transform"), transition: E("transition"), backface: E("backface-visibility"), timing: E("transition-timing-function") }; if (F.transition) { var G = F.timing.dom; if (B.style[G] = l["ease-in-back"][0], !B.style[G]) for (var H in m) l[H][0] = m[H] } var I = b.frame = function () { var a = o.requestAnimationFrame || o.webkitRequestAnimationFrame || o.mozRequestAnimationFrame || o.oRequestAnimationFrame || o.msRequestAnimationFrame; return a && F.bind ? a.bind(o) : function (a) { o.setTimeout(a, 16) } }(), J = b.now = function () { var a = o.performance, b = a && (a.now || a.webkitNow || a.msNow || a.mozNow); return b && F.bind ? b.bind(a) : Date.now || function () { return +new Date } }(), K = k(function (b) {
        function d(a, b) {
            var c = j(("" + a).split(A)), d = c[0]; b = b || {}; var e = X[d]; if (!e) return _("Unsupported property: " + d);
            if (!b.weak || !this.props[d]) {
                var f = e[0], g = this.props[d];
                return g || (g = this.props[d] = new f.Bare), g.init(this.$el, c, e, b), g
            }
        }
        function e(a, b, c) {
            if (a) {
                var e = typeof a;
                if (b || (this.timer && this.timer.destroy(), this.queue = [], this.active = !1), "number" == e && b)
                    return this.timer = new R({ duration: a, context: this, complete: h }), void (this.active = !0);
                if ("string" == e && b) {
                    switch (a) { case "hide": n.call(this); break; case "stop": k.call(this); break; case "redraw": o.call(this); break; default: d.call(this, a, c && c[1]) } return h.call(this)
                }
                if ("function" == e)
                    return void a.call(this, this);
                if ("object" == e) {
                    var f = 0; t.call(this, a, function (a, b) {
                        a.span > f && (f = a.span), a.stop();
                        a.animate(b);
                    },
                        function (a) {
                            "wait" in a && (f = i(a.wait, 0))
                        }), s.call(this), f > 0 && (this.timer = new R({ duration: f, context: this }), this.active = !0, b && (this.timer.complete = h)); var g = this, j = !1, l = {}; I(function () { t.call(g, a, function (a) { a.active && (j = !0, l[a.name] = a.nextStyle) }), j && g.$el.css(l) })
                }
            }
        } function f(a) { a = i(a, 0), this.active ? this.queue.push({ options: a }) : (this.timer = new R({ duration: a, context: this, complete: h }), this.active = !0) } function g(a) { return this.active ? (this.queue.push({ options: a, args: arguments }), void (this.timer.complete = h)) : _("No active transition timer. Use start() or wait() before then().") } function h() { if (this.timer && this.timer.destroy(), this.active = !1, this.queue.length) { var a = this.queue.shift(); e.call(this, a.options, !0, a.args) } } function k(a) { this.timer && this.timer.destroy(), this.queue = [], this.active = !1; var b; "string" == typeof a ? (b = {}, b[a] = 1) : b = "object" == typeof a && null != a ? a : this.props, t.call(this, b, u), s.call(this) } function l(a) { k.call(this, a), t.call(this, a, v, w) } function m(a) { "string" != typeof a && (a = "block"), this.el.style.display = a } function n() { k.call(this), this.el.style.display = "none" } function o() { this.el.offsetHeight } function q() { k.call(this), a.removeData(this.el, p), this.$el = this.el = null } function s() { var a, b, c = []; this.upstream && c.push(this.upstream); for (a in this.props) b = this.props[a], b.active && c.push(b.string); c = c.join(","), this.style !== c && (this.style = c, this.el.style[F.transition.dom] = c) } function t(a, b, e) { var f, g, h, i, j = b !== u, k = {}; for (f in a) h = a[f], f in Y ? (k.transform || (k.transform = {}), k.transform[f] = h) : (r.test(f) && (f = c(f)), f in X ? k[f] = h : (i || (i = {}), i[f] = h)); for (f in k) { if (h = k[f], g = this.props[f], !g) { if (!j) continue; g = d.call(this, f) } b.call(this, g, h) } e && i && e.call(this, i) } function u(a) { a.stop() } function v(a, b) { a.set(b) } function w(a) { this.$el.css(a) } function x(a, c) {
            b[a] = function () {
                //return this.children ? z.call(this, c, arguments) :
                //    (this.el && c.apply(this, arguments), this)
                if (this.children) {
                    return z.call(this, c, arguments);
                } else {
                    return (this.el && c.apply(this, arguments), this);
                }
            }
        } function z(a, b) { var c, d = this.children.length; for (c = 0; d > c; c++) a.apply(this.children[c], b); return this } b.init = function (b) { if (this.$el = a(b), this.el = this.$el[0], this.props = {}, this.queue = [], this.style = "", this.active = !1, T.keepInherited && !T.fallback) { var c = V(this.el, "transition"); c && !y.test(c) && (this.upstream = c) } F.backface && T.hideBackface && U(this.el, F.backface.css, "hidden") }, x("add", d), x("start", e), x("wait", f), x("then", g), x("next", h), x("stop", k), x("set", l), x("show", m), x("hide", n), x("redraw", o), x("destroy", q)
    }), L = k(K, function (b) { function c(b, c) { var d = a.data(b, p) || a.data(b, p, new K.Bare); return d.el || d.init(b), c ? d.start(c) : d } b.init = function (b, d) { var e = a(b); if (!e.length) return this; if (1 === e.length) return c(e[0], d); var f = []; return e.each(function (a, b) { f.push(c(b, d)) }), this.children = f, this } }), M = k(function (a) {
        function b() { var a = this.get(); this.update("auto"); var b = this.get(); return this.update(a), b } function c(a, b, c) { return void 0 !== b && (c = b), a in l ? a : c } function d(a) { var b = /rgba?\((\d+),\s*(\d+),\s*(\d+)/.exec(a); return (b ? e(b[1], b[2], b[3]) : a).replace(/#(\w)(\w)(\w)$/, "#$1$1$2$2$3$3") } var f = { duration: 500, ease: "ease", delay: 0 }; a.init = function (a, b, d, e) { this.$el = a, this.el = a[0]; var g = b[0]; d[2] && (g = d[2]), W[g] && (g = W[g]), this.name = g, this.type = d[1], this.duration = i(b[1], this.duration, f.duration), this.ease = c(b[2], this.ease, f.ease), this.delay = i(b[3], this.delay, f.delay), this.span = this.duration + this.delay, this.active = !1, this.nextStyle = null, this.auto = z.test(this.name), this.unit = e.unit || this.unit || T.defaultUnit, this.angle = e.angle || this.angle || T.defaultAngle, T.fallback || e.fallback ? this.animate = this.fallback : (this.animate = this.transition, this.string = this.name + A + this.duration + "ms" + ("ease" != this.ease ? A + l[this.ease][0] : "") + (this.delay ? A + this.delay + "ms" : "")) }, a.set = function (a) { a = this.convert(a, this.type), this.update(a), this.redraw() }, a.transition = function (a) { this.active = !0, a = this.convert(a, this.type), this.auto && ("auto" == this.el.style[this.name] && (this.update(this.get()), this.redraw()), "auto" == a && (a = b.call(this))), this.nextStyle = a },
            a.fallback = function (a) {
                var c = this.el.style[this.name] || this.convert(this.get(), this.type);
                a = this.convert(a, this.type);
                this.auto && ("auto" == c && (c = this.convert(this.get(), this.type)),
                "auto" == a && (a = b.call(this)));

                //Number(c.replace(/px/gi, ""));
                //Number(b.call(this).replace(/px/gi, ""));
                if (a.indexOf("%") > -1) {
                    c = Math.round(Number(c.replace(/px/gi, "")) / Number(b.call(this).replace(/px/gi, "")) * 100);
                    c += "%"
                }

                this.tween = new Q({ from: c, to: a, duration: this.duration, delay: this.delay, ease: this.ease, update: this.update, context: this })
            }, a.get = function () { return V(this.el, this.name) }, a.update = function (a) { U(this.el, this.name, a) },
        a.stop = function () {
            (this.active || this.nextStyle) && (this.active = !1, this.nextStyle = null, U(this.el, this.name, this.get()));
            var a = this.tween; a && a.context && a.destroy()
        },
        a.convert = function (a, b) {
            if ("auto" == a && this.auto)
                return a;
            var c, e = "number" == typeof a, f = "string" == typeof a;
            switch (b) {
                case s:
                    if (e) return a;
                    if (f && "" === a.replace(q, ""))
                        return +a; c = "number(unitless)";
                    break;
                case t:
                    if (f) {
                        if ("" === a && this.original) return this.original;
                        if (b.test(a))
                            return "#" == a.charAt(0) && 7 == a.length ? a : d(a)
                    }
                    c = "hex or rgb string";
                    break;
                case u:
                    if (e) return a + this.unit;
                    if (f && b.test(a)) return a;
                    c = "number(px) or string(unit)";
                    break;
                case v:
                    if (e) return a + this.unit;
                    if (f && b.test(a)) return a;
                    c = "number(px) or string(unit or %)";
                    break;
                case w:
                    if (e) return a + this.angle;
                    if (f && b.test(a)) return a;
                    c = "number(deg) or string(angle)";
                    break;
                case x:
                    if (e) return a;
                    if (f && v.test(a)) return a;
                    c = "number(unitless) or string(unit or %)"
            }
            return g(c, a), a
        }, a.redraw = function () { this.el.offsetHeight }
    }), N = k(M, function (a, b) { a.init = function () { b.init.apply(this, arguments), this.original || (this.original = this.convert(this.get(), t)) } }), O = k(M, function (a, b) { a.init = function () { b.init.apply(this, arguments), this.animate = this.fallback }, a.get = function () { return this.$el[this.name]() }, a.update = function (a) { this.$el[this.name](a) } }), P = k(M, function (a, b) { function c(a, b) { var c, d, e, f, g; for (c in a) f = Y[c], e = f[0], d = f[1] || c, g = this.convert(a[c], e), b.call(this, d, g, e) } a.init = function () { b.init.apply(this, arguments), this.current || (this.current = {}, Y.perspective && T.perspective && (this.current.perspective = T.perspective, U(this.el, this.name, this.style(this.current)), this.redraw())) }, a.set = function (a) { c.call(this, a, function (a, b) { this.current[a] = b }), U(this.el, this.name, this.style(this.current)), this.redraw() }, a.transition = function (a) { var b = this.values(a); this.tween = new S({ current: this.current, values: b, duration: this.duration, delay: this.delay, ease: this.ease }); var c, d = {}; for (c in this.current) d[c] = c in b ? b[c] : this.current[c]; this.active = !0, this.nextStyle = this.style(d) }, a.fallback = function (a) { var b = this.values(a); this.tween = new S({ current: this.current, values: b, duration: this.duration, delay: this.delay, ease: this.ease, update: this.update, context: this }) }, a.update = function () { U(this.el, this.name, this.style(this.current)) }, a.style = function (a) { var b, c = ""; for (b in a) c += b + "(" + a[b] + ") "; return c }, a.values = function (a) { var b, d = {}; return c.call(this, a, function (a, c, e) { d[a] = c, void 0 === this.current[a] && (b = 0, ~a.indexOf("scale") && (b = 1), this.current[a] = this.convert(b, e)) }), d } }), Q = k(function (b) {
        function c(a) { 1 === n.push(a) && I(g) } function g() { var a, b, c, d = n.length; if (d) for (I(g), b = J(), a = d; a--;) c = n[a], c && c.render(b) } function i(b) { var c, d = a.inArray(b, n); d >= 0 && (c = n.slice(d + 1), n.length = d, c.length && (n = n.concat(c))) } function j(a) { return Math.round(a * o) / o } function k(a, b, c) { return e(a[0] + c * (b[0] - a[0]), a[1] + c * (b[1] - a[1]), a[2] + c * (b[2] - a[2])) } var m = { ease: l.ease[1], from: 0, to: 1 };
        b.init = function (a) {
            this.duration = a.duration || 0,
            this.delay = a.delay || 0;
            var b = a.ease || m.ease;
            l[b] && (b = l[b][1]),
            "function" != typeof b && (b = m.ease),
            this.ease = b,
            this.update = a.update || f,
            this.complete = a.complete || f,
            this.context = a.context || this,
            this.name = a.name;
            var c = a.from,
                d = a.to;
            void 0 === c && (c = m.from);
            void 0 === d && (d = m.to);
            this.unit = a.unit || "";
            "number" == typeof c && "number" == typeof d ? (this.begin = c, this.change = d - c)
                : this.format(d, c);
            this.value = this.begin + this.unit;
            this.start = J();
            a.autoplay !== !1 && this.play();
        }, b.play = function () { this.active || (this.start || (this.start = J()), this.active = !0, c(this)) }, b.stop = function () { this.active && (this.active = !1, i(this)) }, b.render = function (a) { var b, c = a - this.start; if (this.delay) { if (c <= this.delay) return; c -= this.delay } if (c < this.duration) { var d = this.ease(c, 0, 1, this.duration); return b = this.startRGB ? k(this.startRGB, this.endRGB, d) : j(this.begin + d * this.change), this.value = b + this.unit, void this.update.call(this.context, this.value) } b = this.endHex || this.begin + this.change, this.value = b + this.unit, this.update.call(this.context, this.value), this.complete.call(this.context), this.destroy() }, b.format = function (a, b) { if (b += "", a += "", "#" == a.charAt(0)) return this.startRGB = d(b), this.endRGB = d(a), this.endHex = a, this.begin = 0, void (this.change = 1); if (!this.unit) { var c = b.replace(q, ""), e = a.replace(q, ""); c !== e && h("tween", b, a), this.unit = c } b = parseFloat(b), a = parseFloat(a), this.begin = this.value = b, this.change = a - b }, b.destroy = function () { this.stop(), this.context = null, this.ease = this.update = this.complete = f }; var n = [], o = 1e3
    }), R = k(Q, function (a) { a.init = function (a) { this.duration = a.duration || 0, this.complete = a.complete || f, this.context = a.context, this.play() }, a.render = function (a) { var b = a - this.start; b < this.duration || (this.complete.call(this.context), this.destroy()) } }), S = k(Q, function (a, b) { a.init = function (a) { this.context = a.context, this.update = a.update, this.tweens = [], this.current = a.current; var b, c; for (b in a.values) c = a.values[b], this.current[b] !== c && this.tweens.push(new Q({ name: b, from: this.current[b], to: c, duration: a.duration, delay: a.delay, ease: a.ease, autoplay: !1 })); this.play() }, a.render = function (a) { var b, c, d = this.tweens.length, e = !1; for (b = d; b--;) c = this.tweens[b], c.context && (c.render(a), this.current[c.name] = c.value, e = !0); return e ? void (this.update && this.update.call(this.context)) : this.destroy() }, a.destroy = function () { if (b.destroy.call(this), this.tweens) { var a, c = this.tweens.length; for (a = c; a--;) this.tweens[a].destroy(); this.tweens = null, this.current = null } } }), T = b.config = { defaultUnit: "px", defaultAngle: "deg", keepInherited: !1, hideBackface: !1, perspective: "", fallback: !F.transition, agentTests: [] }; b.fallback = function (a) { if (!F.transition) return T.fallback = !0; T.agentTests.push("(" + a + ")"); var b = new RegExp(T.agentTests.join("|"), "i"); T.fallback = b.test(navigator.userAgent) }, b.fallback("6.0.[2-5] Safari"), b.tween = function (a) { return new Q(a) }, b.delay = function (a, b, c) { return new R({ complete: b, duration: a, context: c }) }, a.fn.tram = function (a) { return b.call(null, this, a) }; var U = a.style, V = a.css, W = { transform: F.transform && F.transform.css }, X = { color: [N, t], background: [N, t, "background-color"], "outline-color": [N, t], "border-color": [N, t], "border-top-color": [N, t], "border-right-color": [N, t], "border-bottom-color": [N, t], "border-left-color": [N, t], "border-width": [M, u], "border-top-width": [M, u], "border-right-width": [M, u], "border-bottom-width": [M, u], "border-left-width": [M, u], "border-spacing": [M, u], "letter-spacing": [M, u], margin: [M, u], "margin-top": [M, u], "margin-right": [M, u], "margin-bottom": [M, u], "margin-left": [M, u], padding: [M, u], "padding-top": [M, u], "padding-right": [M, u], "padding-bottom": [M, u], "padding-left": [M, u], "outline-width": [M, u], opacity: [M, s], top: [M, v], right: [M, v], bottom: [M, v], left: [M, v], "font-size": [M, v], "text-indent": [M, v], "word-spacing": [M, v], width: [M, v], "min-width": [M, v], "max-width": [M, v], height: [M, v], "min-height": [M, v], "max-height": [M, v], "line-height": [M, x], "scroll-top": [O, s, "scrollTop"], "scroll-left": [O, s, "scrollLeft"] }, Y = {}; F.transform && (X.transform = [P], Y = { x: [v, "translateX"], y: [v, "translateY"], rotate: [w], rotateX: [w], rotateY: [w], scale: [s], scaleX: [s], scaleY: [s], skew: [w], skewX: [w], skewY: [w] }), F.transform && F.backface && (Y.z = [v, "translateZ"], Y.rotateZ = [w], Y.scaleZ = [s], Y.perspective = [u]); var Z = /ms/, $ = /s|\./, _ = function () { var a = "warn", b = window.console; return b && b[a] ? function (c) { b[a](c) } : f }(); return a.tram = b
}(window.jQuery);
/*!
 * jQuery-ajaxTransport-XDomainRequest - v1.0.3 - 2014-06-06
 * https://github.com/MoonScript/jQuery-ajaxTransport-XDomainRequest
 * Copyright (c) 2014 Jason Moon (@JSONMOON)
 * Licensed MIT (/blob/master/LICENSE.txt)
 */
(function (a) { if (typeof define === 'function' && define.amd) { define(['jquery'], a) } else if (typeof exports === 'object') { module.exports = a(require('jquery')) } else { a(jQuery) } }(function ($) { if ($.support.cors || !$.ajaxTransport || !window.XDomainRequest) { return } var n = /^https?:\/\//i; var o = /^get|post$/i; var p = new RegExp('^' + location.protocol, 'i'); $.ajaxTransport('* text html xml json', function (j, k, l) { if (!j.crossDomain || !j.async || !o.test(j.type) || !n.test(j.url) || !p.test(j.url)) { return } var m = null; return { send: function (f, g) { var h = ''; var i = (k.dataType || '').toLowerCase(); m = new XDomainRequest(); if (/^\d+$/.test(k.timeout)) { m.timeout = k.timeout } m.ontimeout = function () { g(500, 'timeout') }; m.onload = function () { var a = 'Content-Length: ' + m.responseText.length + '\r\nContent-Type: ' + m.contentType; var b = { code: 200, message: 'success' }; var c = { text: m.responseText }; try { if (i === 'html' || /text\/html/i.test(m.contentType)) { c.html = m.responseText } else if (i === 'json' || (i !== 'text' && /\/json/i.test(m.contentType))) { try { c.json = $.parseJSON(m.responseText) } catch (e) { b.code = 500; b.message = 'parseerror' } } else if (i === 'xml' || (i !== 'text' && /\/xml/i.test(m.contentType))) { var d = new ActiveXObject('Microsoft.XMLDOM'); d.async = false; try { d.loadXML(m.responseText) } catch (e) { d = undefined } if (!d || !d.documentElement || d.getElementsByTagName('parsererror').length) { b.code = 500; b.message = 'parseerror'; throw 'Invalid XML: ' + m.responseText; } c.xml = d } } catch (parseMessage) { throw parseMessage; } finally { g(b.code, b.message, c, a) } }; m.onprogress = function () { }; m.onerror = function () { g(500, 'error', { text: m.responseText }) }; if (k.data) { h = ($.type(k.data) === 'string') ? k.data : $.param(k.data) } m.open(j.type, j.url); m.send(h) }, abort: function () { if (m) { m.abort() } } } }) }));
/* jshint ignore:end */
/**
 * ----------------------------------------------------------------------
 * Init lib after plugins
 */
Mica.init();

/**
 * ----------------------------------------------------------------------
 * Mica: Multi-language
 */

Mica.define('lang', function ($, _) {
    //'use strict';

    var api = {};
    var $win = $(window);
    var countryCode = "";
    var designer = "";

    // -----------------------------------
    // Module methods
    api.init = function (list) {
        setTimeout(function () { init(); }, 1);
    };

    api.preview = function () {
        designer = false;
        setTimeout(function () { init(); }, 1);
    };

    api.design = function () {
        designer = true;
    };

    api.ready = function () {
        //1201-sy : OPEN MES_ toUpperCase() undefined 예외 처리
        if (!Mica.lang()) return;

        countryCode = Mica.lang().toUpperCase();
        if (!countryCode) return;
        //$.getScript('/Content/js/regional.js', function () {
        init();
        //});
    };

    api.apply = function (colModel) {
        if (!countryCode) return;
        //$.getScript('/Content/js/regional.js', function () {
        _.each(colModel, function (item) {
            if (!(!MULTI_LANGUAGE_DATA)) {
                var value = MULTI_LANGUAGE_DATA[codeId] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] : undefined : undefined;

                item.name && typeof (value) != undefined && (item.label = value);
            }
        });
        //});
        return colModel;
    };

    function init() {
        MULTI_LANGUAGE_CODE = sessionStorage.getItem("LANGUAGE") ? sessionStorage.getItem("LANGUAGE") : countryCode ? countryCode : null;
        if (!MULTI_LANGUAGE_CODE) return;
        // if ('function' == typeof setPageMultiLang) { setPageMultiLang(); }
        MULTI_LANGUAGE_DATA = micaCommon.multiLanguage.get();
        var els = $('[multi-lang], [placeholder], [title]');
        if (!els.length) return;
        els.each(build);
        //        $("head").append('<style> [multi-lang]{display:block}</style>');
        $("style#multi-lang").remove();
    }

    function build(i, el) {
        var EL = $(el);
        var codeId = "";
        if (EL.attr('multi-lang')) {
            codeId = EL.attr('multi-lang');
            if (!(!MULTI_LANGUAGE_DATA)) {
                var value = MULTI_LANGUAGE_DATA[codeId] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] : undefined : undefined;
                codeId && typeof (value) != undefined && EL.html(value);
            }
        }
        if (EL.attr('placeholder')) {
            codeId = EL.attr('placeholder');
            if (!(!MULTI_LANGUAGE_DATA)) {
                var value = MULTI_LANGUAGE_DATA[codeId] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] : undefined : undefined;
                codeId && typeof (value) != undefined && EL.attr("placeholder", value);
            }
        }
        if (EL.attr('title')) {
            codeId = EL.attr('title');
            if (!(!MULTI_LANGUAGE_DATA)) {
                var value = MULTI_LANGUAGE_DATA[codeId] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] ? MULTI_LANGUAGE_DATA[codeId][MULTI_LANGUAGE_CODE] : undefined : undefined;
                codeId && typeof (value) != undefined && EL.attr("title", value);
            }
        }
    }
    // Export module
    return api;
});

/**
 * ----------------------------------------------------------------------
 * Mica: Interactions
 */
Mica.define('ix', function ($, _) {
    //'use strict';

    var api = {};
    var designer;
    var $win = $(window);
    var namespace = '.w-ix';
    var tram = window.tram;
    var env = Mica.env;
    var ios = env.ios;
    var inApp = env();
    var emptyFix = env.chrome && env.chrome < 35;
    var transNone = 'none 0s ease 0s';
    var introEvent = 'w-ix-intro' + namespace;
    var outroEvent = 'w-ix-outro' + namespace;
    var fallbackProps = /width|height/;
    var eventQueue = [];
    var $subs = $();
    var config = {};
    var anchors = [];
    var loads = [];
    var readys = [];
    var destroyed;

    // Component types and proxy selectors
    var components = {
        tabs: '.w-tab-link, .w-tab-pane',
        dropdown: '.w-dropdown',
        slider: '.w-slide',
        navbar: '.w-nav'
    };

    // -----------------------------------
    // Module methods

    api.init = function (list) {
        setTimeout(function () { configure(list); }, 1);
    };

    api.preview = function () {
        designer = false;
        setTimeout(function () { configure(window.__wf_ix); }, 1);
    };

    api.design = function () {
        designer = true;
        api.destroy();
    };

    api.destroy = function () {
        destroyed = true;
        $subs.each(teardown);
        Mica.scroll.off(scroll);
        asyncEvents();
        anchors = [];
        loads = [];
        readys = [];
    };

    api.ready = function () {
        // Ready should only be used after destroy, as a way to re-init
        if (config && destroyed) {
            destroyed = false;
            init();
        }
    };

    api.run = run;
    api.events = {};
    api.style = inApp ? styleApp : stylePub;

    // -----------------------------------
    // Private methods

    function configure(list) {
        if (!list) return;

        // Map all interactions to a hash using slug as key.
        config = {};
        _.each(list, function (item) {
            config[item.slug] = item.value;
        });

        // Init ix after config
        init();
    }

    function init() {
        // Build each element's interaction keying from data attribute
        var els = $('[data-ix]');
        if (!els.length) return;
        els.each(teardown);
        els.each(build);

        // Listen for scroll events if any anchors exist
        if (anchors.length) {
            Mica.scroll.on(scroll);
            setTimeout(scroll, 1);
        }

        // Handle loads or readys if they exist
        if (loads.length) Mica.load(runLoads);
        if (readys.length) setTimeout(runReadys, 1);

        // Trigger queued events, must happen after init
        initEvents();
    }

    function build(i, el) {
        var $el = $(el);
        var id = $el.attr('data-ix');
        var ix = config[id];
        if (!ix) return;
        var triggers = ix.triggers;
        if (!triggers) return;

        // Set initial styles, unless we detect an iOS device + any non-iOS triggers
        var setStyles = !(ios && _.any(triggers, isNonIOS));
        if (setStyles) api.style($el, ix.style);

        _.each(triggers, function (trigger) {
            var state = {};
            var type = trigger.type;
            var stepsB = trigger.stepsB && trigger.stepsB.length;

            function runA() { run(trigger, $el, { group: 'A' }); }
            function runB() { run(trigger, $el, { group: 'B' }); }

            if (type == 'load') {
                (trigger.preload && !inApp) ? loads.push(runA) : readys.push(runA);
                return;
            }

            if (type == 'click') {
                $el.on('click' + namespace, function (evt) {
                    // Avoid late clicks on touch devices
                    if (!Mica.validClick(evt.currentTarget)) return;

                    // Prevent default on empty hash urls
                    if ($el.attr('href') === '#') evt.preventDefault();

                    run(trigger, $el, { group: state.clicked ? 'B' : 'A' });
                    if (stepsB) state.clicked = !state.clicked;
                });
                $subs = $subs.add($el);
                return;
            }

            if (type == 'hover') {
                $el.on('mouseenter' + namespace, runA);
                $el.on('mouseleave' + namespace, runB);
                $subs = $subs.add($el);
                return;
            }

            // Check for a component proxy selector
            var proxy = components[type];
            if (proxy) {
                var $proxy = $el.closest(proxy);
                $proxy.on(introEvent, runA).on(outroEvent, runB);
                $subs = $subs.add($proxy);
                return;
            }

            // Ignore the following triggers on iOS devices
            if (ios) return;

            if (type == 'scroll') {
                anchors.push({
                    el: $el, trigger: trigger, state: { active: false },
                    offsetTop: convert(trigger.offsetTop),
                    offsetBot: convert(trigger.offsetBot)
                });
                return;
            }
        });
    }

    function isNonIOS(trigger) {
        return trigger.type == 'scroll';
    }

    function convert(offset) {
        if (!offset) return 0;
        offset = offset + '';
        var result = parseInt(offset, 10);
        if (result !== result) return 0;
        if (offset.indexOf('%') > 0) {
            result = result / 100;
            if (result >= 1) result = 0.999;
        }
        return result;
    }

    function teardown(i, el) {
        $(el).off(namespace);
    }

    function scroll() {
        var viewTop = $win.scrollTop();
        var viewHeight = $win.height();

        // Check each anchor for a valid scroll trigger
        var count = anchors.length;
        for (var i = 0; i < count; i++) {
            var anchor = anchors[i];
            var $el = anchor.el;
            var trigger = anchor.trigger;
            var stepsB = trigger.stepsB && trigger.stepsB.length;
            var state = anchor.state;
            var top = $el.offset().top;
            var height = $el.outerHeight();
            var offsetTop = anchor.offsetTop;
            var offsetBot = anchor.offsetBot;
            if (offsetTop < 1 && offsetTop > 0) offsetTop *= viewHeight;
            if (offsetBot < 1 && offsetBot > 0) offsetBot *= viewHeight;
            var active = (top + height - offsetTop >= viewTop && top + offsetBot <= viewTop + viewHeight);
            if (active === state.active) continue;
            if (active === false && !stepsB) continue;
            state.active = active;
            run(trigger, $el, { group: active ? 'A' : 'B' });
        }
    }

    function runLoads() {
        var count = loads.length;
        for (var i = 0; i < count; i++) {
            loads[i]();
        }
    }

    function runReadys() {
        var count = readys.length;
        for (var i = 0; i < count; i++) {
            readys[i]();
        }
    }

    function run(trigger, $el, opts, replay) {
        opts = opts || {};
        var done = opts.done;

        // Do not run in designer unless forced
        if (designer && !opts.force) return;

        // Operate on a set of grouped steps
        var group = opts.group || 'A';
        var loop = trigger['loop' + group];
        var steps = trigger['steps' + group];
        if (!steps || !steps.length) return;
        if (steps.length < 2) loop = false;

        // One-time init before any loops
        if (!replay) {

            // Find selector within element descendants, siblings, or query whole document
            var selector = trigger.selector;
            if (selector) {
                $el = (
                  trigger.descend ? $el.find(selector) :
                  trigger.siblings ? $el.siblings(selector) :
                  $(selector)
                );
                if (inApp) $el.attr('data-ix-affect', 1);
            }

            // Apply empty fix for certain Chrome versions
            if (emptyFix) $el.addClass('w-ix-emptyfix');
        }

        var _tram = tram($el);

        // Add steps
        var meta = {};
        for (var i = 0; i < steps.length; i++) {
            addStep(_tram, steps[i], meta);
        }

        function fin() {
            // Run trigger again if looped
            if (loop) return run(trigger, $el, opts, true);

            // Reset any 'auto' values
            if (meta.width == 'auto') _tram.set({ width: 'auto' });
            if (meta.height == 'auto') _tram.set({ height: 'auto' });

            // Run callback
            done && done();
        }

        // Add final step to queue if tram has started
        meta.start ? _tram.then(fin) : fin();
    }

    function addStep(_tram, step, meta) {
        var addMethod = 'add';
        var startMethod = 'start';

        // Once the transition has started, we will always use then() to add to the queue.
        if (meta.start) addMethod = startMethod = 'then';

        // Parse transitions string on the current step
        var transitions = step.transition;
        if (transitions) {
            transitions = transitions.split(',');
            for (var i = 0; i < transitions.length; i++) {
                var transition = transitions[i];
                var options = fallbackProps.test(transition) ? { fallback: true } : null;
                _tram[addMethod](transition, options);
            }
        }

        // Build a clean object to pass to the tram method
        var clean = tramify(step) || {};

        // Store last width and height values
        if (clean.width != null) meta.width = clean.width;
        if (clean.height != null) meta.height = clean.height;

        // When transitions are not present, set values immediately and continue queue.
        if (transitions == null) {

            // If we have started, wrap set() in then() and reset queue
            if (meta.start) {
                _tram.then(function () {
                    var queue = this.queue;
                    this.set(clean);
                    if (clean.display) {
                        _tram.redraw();
                        Mica.redraw.up();
                    }
                    this.queue = queue;
                    this.next();
                });
            } else {
                _tram.set(clean);

                // Always redraw after setting display
                if (clean.display) {
                    _tram.redraw();
                    Mica.redraw.up();
                }
            }

            // Use the wait() method to kick off queue in absence of transitions.
            var wait = clean.wait;
            if (wait != null) {
                _tram.wait(wait);
                meta.start = true;
            }

            // Otherwise, when transitions are present
        } else {

            // If display is present, handle it separately
            if (clean.display) {
                var display = clean.display;
                delete clean.display;

                // If we've already started, we need to wrap it in a then()
                if (meta.start) {
                    _tram.then(function () {
                        var queue = this.queue;
                        this.set({ display: display }).redraw();
                        Mica.redraw.up();
                        this.queue = queue;
                        this.next();
                    });
                } else {
                    _tram.set({ display: display }).redraw();
                    Mica.redraw.up();
                }
            }

            // Otherwise, start a transition using the current start method.
            _tram[startMethod](clean);
            meta.start = true;
        }
    }

    // (In app) Set styles immediately and manage upstream transition
    function styleApp(el, data) {
        var _tram = tram(el);

        // Get computed transition value
        el.css('transition', '');
        var computed = el.css('transition');

        // If computed is disabled, clear upstream
        if (computed === transNone) computed = _tram.upstream = null;

        // Disable upstream temporarily
        _tram.upstream = transNone;

        // Set values immediately
        _tram.set(tramify(data));

        // Only restore upstream in preview mode
        _tram.upstream = computed;
    }

    // (Published) Set styles immediately on specified jquery element
    function stylePub(el, data) {
        tram(el).set(tramify(data));
    }

    // Build a clean object for tram
    function tramify(obj) {
        var result = {};
        var found = false;
        for (var x in obj) {
            if (x === 'transition') continue;
            result[x] = obj[x];
            found = true;
        }
        // If empty, return null for tram.set/stop compliance
        return found ? result : null;
    }

    // Events used by other mica modules
    var events = {
        reset: function (i, el) {
            el.__wf_intro = null;
        },
        intro: function (i, el) {
            if (el.__wf_intro) return;
            el.__wf_intro = true;
            $(el).triggerHandler(introEvent);
        },
        outro: function (i, el) {
            if (!el.__wf_intro) return;
            el.__wf_intro = null;
            $(el).triggerHandler(outroEvent);
        }
    };

    // Trigger events in queue + point to sync methods
    function initEvents() {
        var count = eventQueue.length;
        for (var i = 0; i < count; i++) {
            var memo = eventQueue[i];
            memo[0](0, memo[1]);
        }
        eventQueue = [];
        $.extend(api.events, events);
    }

    // Replace events with async methods prior to init
    function asyncEvents() {
        _.each(events, function (func, name) {
            api.events[name] = function (i, el) {
                eventQueue.push([func, el]);
            };
        });
    }

    asyncEvents();

    // Export module
    return api;
});


/**
 * ----------------------------------------------------------------------
 * Mica: Touch events
 */
Mica.define('touch', function ($, _) {
    //'use strict';

    var api = {};
    var fallback = !document.addEventListener;
    var getSelection = window.getSelection;

    // Fallback to click events in old IE
    if (fallback) {
        $.event.special.tap = { bindType: 'click', delegateType: 'click' };
    }

    api.init = function (el) {
        if (fallback) return null;
        el = typeof el === 'string' ? $(el).get(0) : el;
        return el ? new Touch(el) : null;
    };

    function Touch(el) {
        var active = false;
        var dirty = false;
        var useTouch = false;
        var thresholdX = Math.min(Math.round(window.innerWidth * 0.04), 40);
        var startX, startY, lastX;

        el.addEventListener('touchstart', start, false);
        el.addEventListener('touchmove', move, false);
        el.addEventListener('touchend', end, false);
        el.addEventListener('touchcancel', cancel, false);
        el.addEventListener('mousedown', start, false);
        el.addEventListener('mousemove', move, false);
        el.addEventListener('mouseup', end, false);
        el.addEventListener('mouseout', cancel, false);

        function start(evt) {
            // We don’t handle multi-touch events yet.
            var touches = evt.touches;
            if (touches && touches.length > 1) {
                return;
            }

            active = true;
            dirty = false;

            if (touches) {
                useTouch = true;
                startX = touches[0].clientX;
                startY = touches[0].clientY;
            } else {
                startX = evt.clientX;
                startY = evt.clientY;
            }

            lastX = startX;
        }

        function move(evt) {
            if (!active) return;

            if (useTouch && evt.type === 'mousemove') {
                evt.preventDefault();
                evt.stopPropagation();
                return;
            }

            var touches = evt.touches;
            var x = touches ? touches[0].clientX : evt.clientX;
            var y = touches ? touches[0].clientY : evt.clientY;

            var velocityX = x - lastX;
            lastX = x;

            // Allow swipes while pointer is down, but prevent them during text selection
            if (Math.abs(velocityX) > thresholdX && getSelection && getSelection() + '' === '') {
                triggerEvent('swipe', evt, { direction: velocityX > 0 ? 'right' : 'left' });
                cancel();
            }

            // If pointer moves more than 10px flag to cancel tap
            if (Math.abs(x - startX) > 10 || Math.abs(y - startY) > 10) {
                dirty = true;
            }
        }

        function end(evt) {
            if (!active) return;
            active = false;

            if (useTouch && evt.type === 'mouseup') {
                evt.preventDefault();
                evt.stopPropagation();
                useTouch = false;
                return;
            }

            if (!dirty) triggerEvent('tap', evt);
        }

        function cancel(evt) {
            active = false;
        }

        function destroy() {
            el.removeEventListener('touchstart', start, false);
            el.removeEventListener('touchmove', move, false);
            el.removeEventListener('touchend', end, false);
            el.removeEventListener('touchcancel', cancel, false);
            el.removeEventListener('mousedown', start, false);
            el.removeEventListener('mousemove', move, false);
            el.removeEventListener('mouseup', end, false);
            el.removeEventListener('mouseout', cancel, false);
            el = null;
        }

        // Public instance methods
        this.destroy = destroy;
    }

    // Wrap native event to supoprt preventdefault + stopPropagation
    function triggerEvent(type, evt, data) {
        var newEvent = $.Event(type, { originalEvent: evt });
        $(evt.target).trigger(newEvent, data);
    }

    // Listen for touch events on all nodes by default.
    api.instance = api.init(document);

    // Export module
    return api;
});


/**
 * ----------------------------------------------------------------------
 * Mica: Data-Forms
 */
if ($.fn.mask) {
    $('[data-mask]').each(function () {

        $this = $(this);
        var mask = $this.attr('data-mask') || 'error...', mask_placeholder = $this.attr('data-mask-placeholder') || 'X';

        $this.mask(mask, {
            placeholder: mask_placeholder
        });
    })
}

Mica.define('forms', function ($, _) {
    //'use strict';

    var api = {};
    var $doc = $(document);
    var $forms;
    var loc = window.location;
    var retro = window.XDomainRequest && !window.atob;
    var namespace = '.w-form';
    var siteId;
    var alert = window.alert;
    var listening;

    api.ready = function () {
        init();
        // Wire document events once
        if (!listening) addListeners();
    };

    // 디자인이나 프리뷰시 보일껀지
    //api.preview = api.design = function () {
    //       init();
    //   };

    function init() {
        $forms = $(namespace + ' form');
        if (!$forms.length) return;
        $forms.each(build);
    }

    function build(i, form) {
        // Store form state using namespace
        var formEL = $(form);
        var data = $.data(form, namespace);
        if (!data) data = $.data(form, namespace, { form: formEL }); // data.form

        data.action = formEL.attr('action');
        data.handler = null;
        data.redirect = formEL.attr('data-redirect');

        formEL.find(":input:not([type='submit'])").each(function (i, el) {
            var $inputEL = $(el);
            $inputEL.attr('action') && (data.action = $inputEL.attr('action')); //버튼에 폼액션 추가 가능
            //$inputEL.attr('value') && $inputEL.val(""); //초기화

            // data-type 디비 입력에 맞게 수정.
            switch ($inputEL.attr("input-type")) {
                case "datepicker":
                    var dataFormat = $inputEL.attr("data-format") || 'Y-m-d';
                    $.datetimepicker.setLocale('ko');
                    var dateFormat = ($inputEL.attr("date-format") || "").toLocaleLowerCase();
                    //dateFormat

                    var options = {
                        format: dataFormat,
                        formatDate: dataFormat,
                        step: 60
                    };
                    if (dateFormat.indexOf("time") < 0 && dateFormat.indexOf("date") < 0) {
                        $.extend(options, {
                            timepicker: dateFormat.indexOf("time") > -1,
                            datepicker: true
                        });
                    } else {
                        $.extend(options, {
                            timepicker: dateFormat.indexOf("time") > -1,
                            datepicker: dateFormat.indexOf("date") > -1,
                        });

                    }
                    if (dataFormat == "Y-m") {
                        $.extend(options, {
                            onGenerate: function (a, b) {
                                //$(".xdsoft_datetimepicker[style*='display: block']");
                                var clone = $($(".xdsoft_datetimepicker[style*='display: block']").find("tr")[2]).find("td:first").clone();
                                clone.css("width", "1%");
                                clone.html("<div style='text-align:center'>OK</div>");
                                $(".xdsoft_datetimepicker[style*='display: block']").find(".xdsoft_calendar").html(clone);
                            },
                            onShow: function (a, b) {
                                var picker = $(".xdsoft_datetimepicker");
                                $.each(picker, function (i, v) {
                                    var clone = $($(v).find("tr")[2]).find("td:first").clone();
                                    clone.css("width", "1%");
                                    clone.html("<div style='text-align:center'>OK</div>");
                                    $(v).find(".xdsoft_calendar").html(clone);
                                });
                            }
                        });
                    }
                    $inputEL.datetimepicker(options);


                    //var dateFormat = $inputEL.attr("data-format") || 'yy-mm-dd';
                    //if (dateFormat == "yymm" || dateFormat == "yy-mm" || dateFormat == "yy/mm") {
                    //    $inputEL.datepicker({
                    //        dateFormat: dateFormat,
                    //        changeMonth: true,
                    //        changeYear: true,
                    //        showButtonPanel: true,
                    //    });
                    //    $inputEL.focus(function () {
                    //        var thisCalendar = $(this);
                    //        $('.ui-datepicker-calendar').detach();
                    //        $("#ui-datepicker-div").position({
                    //            my: "center top",
                    //            at: "center bottom",
                    //            of: $(this)
                    //        });
                    //        $(".ui-datepicker-current").hide();
                    //        $('.ui-datepicker-close').click(function () {
                    //            var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
                    //            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
                    //            thisCalendar.datepicker('setDate', new Date(year, month, 1));
                    //            $(".ui-datepicker").hide();
                    //        });
                    //    });

                    //} else {
                    //    $inputEL.datepicker({
                    //        dateFormat: dateFormat
                    //    });
                    //}
                    break;
                case "numbercustom":
                    $inputEL.numeric();
                    break;
            }
        });

        var selectBoxcallback = function (dynEL, data) {
            if (data.success) {
                var list = data.result.rows || data.result;
                if (micaConfig.mobile) {
                } else {
                    var num = dynEL.jqxComboBox("getItems").length;
                    for (var i = 0; i < num; i++) {
                        dynEL.jqxComboBox("removeAt", 0);
                    }
                    if (list.count < 8)
                        $selectEL.jqxComboBox({ dropDownHeight: list.count * 26 });
                }
                dynEL.html("");
                //dynEL.select2();
                _.each(list, function (item) {
                    //$.each(item, function (name, value) {
                    //    if (value == "") {
                    //        item[name] = " ";
                    //    }
                    //});
                    //var value = item.ID || item.id || item.VALUE || item.value || item.key;
                    var dynamicvalue = dynEL.attr("dynamicvalue");
                    var dynamictext = dynEL.attr("dynamictext");
                    var value = item[dynamicvalue] != null ? item[dynamicvalue] : item.ID != null ? item.ID : item.id != null ? item.id : item.VALUE != null ? item.VALUE : item.value != null ? item.value : item.key;
                    //var text = item.TEXT || item.text || item.name;
                    var text = item[dynamictext] != null ? item[dynamictext] : item.TEXT != null ? item.TEXT : item.text != null ? item.text : item.name;
                    var html = "<option value='#{value}'>#{text}</option>";
                    html = html.replace(/#{value}/gi, value).replace(/#{text}/gi, text);
                    dynEL.append(html);

                    if (micaConfig.mobile) {
                    } else {
                        dynEL.jqxComboBox("addItem", { label: text, value: value });
                    }

                });

            }
        };

        $("select", formEL).each(function (i, el) {
            var $selectEL = $(el);
            $selectEL.removeClass("w-select");

            var width = null;
            var cssName = $("html").attr("data-site");
            var classNames = $selectEL.attr("class").split(" ");
            var className = ""; //클래스명이 붙음.

            for (var i = 0; i < classNames.length; i++) {
                className += "." + classNames[i];
            }
            width = micaCommon.fncS.cssStyleGet(cssName, className, "width") || null;
            className = "";
            if (width == null) {
                for (var i = 0; i < classNames.length; i++) {
                    if (classNames[i] != "") {
                        if (width == null) {
                            width = micaCommon.fncS.cssStyleGet(cssName, "." + classNames[i], "width") || null;
                        }
                    }
                    //className += "." + classNames[i];
                }
            }
            width = width || $selectEL.width();

            width -= 2;
            /* width % 구하기 */
            //var parentWidth = $selectEL.offsetParent().width();
            //width = 100 * width / parentWidth;
            //width = "50%";
            var height = $selectEL.height();
            if (height < 24) height = 24;

            var dataSetID = $selectEL.attr("data-dynamic");
            var onClick = $selectEL.attr("data-request");

            // multiple 임시 적용
            if ($selectEL.attr("input-type") == "multiple") {

                if (dataSetID != null) {
                    var newID = $selectEL.attr("id");
                    if (micaConfig.mobile) {
                        $selectEL.addClass("w-select");
                    } else {
                        $selectEL.jqxComboBox({ multiSelect: true, checkboxes: true, selectedIndex: 0, width: width, height: height });
                    }
                    //$selectEL.jqxComboBox({ checkboxes: true, selectedIndex: 0, width: width, height: height });
                    var originalClass = $selectEL.attr("class");
                    originalClass && newID && $("#" + newID).addClass(originalClass);

                    if (onClick == 'y') {
                        //                        $selectEL.on('open', function (event) {
                        $("#" + newID).on('open', function (event) {
                            var args = event.args;
                            if (args != undefined) {
                                var item = event.args.item;
                                if (item != null) {
                                    $('#Events').jqxPanel('prepend', '<div style="margin-top: 5px;">Selected: ' + item.label + '</div>');
                                }
                            }
                            $.when(Mica.require('dataSet').requestData(dataSetID.toLowerCase())).then(function (data) {
                                selectBoxcallback($selectEL, data);
                            });
                        });
                    }
                    else {
                        $.when(Mica.require('dataSet').requestData(dataSetID.toLowerCase())).then(function (data) {
                            selectBoxcallback($selectEL, data);
                        });
                    }
                }
                else {
                    var newID = $selectEL.attr("id");
                    var itemCount = $('#' + newID + ' option') ? $('#' + newID + ' option').size() : 1;
                    if (itemCount > 8) itemCount = 8;
                    //$selectEL.jqxComboBox({ multiSelect: true, checkboxes: true, selectedIndex: 0, width: width, height: height, dropDownHeight: itemCount * 26 });
                    //var originalClass = $selectEL.attr("class");
                    //originalClass && newID && $("#" + newID).addClass(originalClass);
                    if (micaConfig.mobile) {
                        $selectEL.addClass("w-select");
                    } else {
                        micaCommon.comboGridBox.set($selectEL, { multiSelect: true, checkboxes: true, selectedIndex: 0, width: width, height: height, dropDownHeight: itemCount * ($($selectEL).height() < 18 ? 32 : $($selectEL).height()) },
                            { local: local, textName: "text", valueName: "value", gridStyle: { color: "#777B8C" }, gridClass: $selectEL.attr("class") });
                        $("#" + newID).css("color", "#777B8C");
                    }
                }

                //$selectEL.attr("multiple", "multiple");
                //$selectEL.select2();
                //$selectEL.jqxComboBox({ multiSelect: true, checkboxes: true, selectedIndex: 0, width: width, height: height });
                //var newID = $selectEL.attr("id");
                //var originalClass = $selectEL.attr("class");
                //originalClass && newID && $("#" + newID).addClass(originalClass);
            }
            else {
                //$selectEL.select2();
                if (dataSetID != null) {
                    var newID = $selectEL.attr("id");
                    if (micaConfig.mobile) {
                        $selectEL.addClass("w-select");
                    } else {
                        $selectEL.jqxComboBox({ selectedIndex: 0, width: width, height: height });
                    }
                    //$selectEL.jqxComboBox({ checkboxes: true, selectedIndex: 0, width: width, height: height });
                    var originalClass = $selectEL.attr("class");
                    originalClass && newID && $("#" + newID).addClass(originalClass);

                    if (onClick == 'y') {
                        //                        $selectEL.on('open', function (event) {
                        $("#" + newID).on('open', function (event) {
                            var args = event.args;
                            if (args != undefined) {
                                var item = event.args.item;
                                if (item != null) {
                                    $('#Events').jqxPanel('prepend', '<div style="margin-top: 5px;">Selected: ' + item.label + '</div>');
                                }
                            }
                            $.when(Mica.require('dataSet').requestData(dataSetID.toLowerCase())).then(function (data) {
                                selectBoxcallback($selectEL, data);
                            });
                        });
                    }
                    else {
                        $.when(Mica.require('dataSet').requestData(dataSetID.toLowerCase())).then(function (data) {
                            selectBoxcallback($selectEL, data);
                        });
                    }
                }
                else {
                    var newID = $selectEL.attr("id");
                    var itemCount = $('#' + newID + ' option') ? $('#' + newID + ' option').size() : 1;
                    if (itemCount > 8) itemCount = 8;
                    var options = $selectEL.find("option");
                    var local = [];
                    $.each(options, function (i, v) {
                        v = $(v);
                        local.push({ text: v.text(), value: v.val() });
                    });
                    //$selectEL.jqxComboBox({ selectedIndex: 0, width: width, height: height, dropDownHeight: itemCount * 26 });
                    // animationType: "none",
                    if (micaConfig.mobile) {
                        $selectEL.addClass("w-select");
                    } else {
                        micaCommon.comboGridBox.set($selectEL, { selectedIndex: 0, width: width, height: height, dropDownHeight: itemCount * ($($selectEL).height() < 18 ? 32 : $($selectEL).height()) },
                            { local: local, textName: "text", valueName: "value", gridStyle: { color: "#777B8C" }, gridClass: $selectEL.attr("class") });
                    }
                    //micaCommon.comboGridBox.set($selectEL, { selectedIndex: 0, width: width, height: height, dropDownHeight: itemCount * ($($selectEL).height() == 16 ? 31 : $($selectEL).height()) },
                    //    { local: local, textName: "text", valueName: "value",gridStyle:{color:"#777B8C"}, gridClass: $selectEL.attr("class") },
                    //    {
                    //        after: function (a, b, c, d) {
                    //            $("#"+$selectEL.attr("id").replace(/_jqxComboBox/,"")).find(".jqx-combobox-content").on("click",function(){
                    //                $("#" + $selectEL.attr("id").replace(/_jqxComboBox/, "")).find(".jqx-combobox-arrow-normal").trigger("mousedown");
                    //            });
                    //            //debugger;
                    //        }
                    //    });

                    $("#" + newID).css("color", "#777B8C");
                    //var originalClass = $selectEL.attr("class");
                    //originalClass && newID && $("#" + newID).addClass(originalClass);
                }
            }

            $("#" + $selectEL.attr("name")).find("input").attr("readonly", true);
        });
        //$(".select2-arrow").hide();
        data.handler = submitForm; return;
    }

    function addListeners() {
        listening = true;

        // Handle form submission for Mica forms
        $doc.on('submit', namespace + ' form', function (evt) {
            var data = $.data(this, namespace);
            if (data.handler) {
                data.evt = evt;
                data.handler(data);
            }
        });
    }

    // Reset data common to all submit handlers
    function reset(data) {
        var btn = data.btn = data.form.find(':input[type="submit"]');
        data.wait = data.btn.attr('data-wait') || null;
        data.success = false;
        btn.prop('disabled', false);
        data.label && btn.val(data.label);
    }

    // Disable submit button
    function disableBtn(data) {
        var btn = data.btn;
        var wait = data.wait;
        btn.prop('disabled', true);
        // Show wait text and store previous label
        if (wait) {
            data.label = btn.val();
            btn.val(wait);
        }
    }

    // Find form fields, validate, and set value pairs
    function findFields(form, result) {
        var status = null;
        result = result || {};
        var num = 0;
        // The ":input" selector is a jQuery shortcut to select all inputs, selects, textareas
        form.find(':input:not([type="submit"], [autocomplete="off"])').each(function (i, el) {
            var field = $(el);
            var type = field.attr('type') || field.attr('input-type');
            var name = field.attr('name') || field.attr('data-name') || ('Field ' + (i + 1));
            var value = field.val() || "";
            //            var value = field.val() || [];
            if (type == 'multiple') {
                if (typeof value != 'string') {
                    var buff = "";
                    for (var count = 0; count < value.length; count++) {
                        buff += value[count];
                        if (count < value.length - 1) buff += ",";
                    }
                    value = buff;
                }
            } else if (type == 'checkbox') {
                name = field.attr('data-name');
                value = field.is(':checked');
                if (value) {
                    value = [field.val()];
                } else {
                    value = null;
                }
            } else if (type == 'radio') {
                name = field.attr('data-name');
                value = field.is(':checked');
                if (value) {
                    value = field.val();
                } else {
                    value = null;
                }
                //if (result[name] === null || typeof result[name] == 'string') {
                //    return;
                //}
                //value = form.find('input[name="' + field.attr('name') + '"]:checked').val() || null;
            }

            if (typeof value == 'string') value = $.trim(value);

            //if (value != "") {

            //result[num++] = {
            if (result[name] == null) {
                result[name] = {
                    "name": name,
                    "value": value
                }
            } else {
                if (typeof result[name]["value"] == "string") {
                    if (result[name]["value"] == null) {
                        result[name]["value"] = value;
                    }
                } else if (result[name]["value"] == null) {
                    result[name]["value"] = value;
                }
                else {
                    if (value != null) {
                        if (result[name]["value"] == null) {
                            result[name]["value"] = [value[0]];
                        } else {
                            result[name]["value"].push(value[0]);
                        }
                    }
                }
            }
            //}
            status = status || getStatus(field, name, value);
        });
        console.log(result);
        return status;
    }

    function getStatus(field, name, value) {
        var status = null;
        if (!field.attr('required')) return null;
        if (!value) status = 'Please fill out the required field: ' + name;
        return status;
    }

    // Submit form to Mica
    function submitForm(data) {
        reset(data);

        disableBtn(data);

        var form = data.form;
        var payload = {
            name: form.attr('name') || form.attr('data-name') || 'Untitled Form',
            test: Mica.env(),
            fields: {}
        };
        preventDefault(data);

        // Find & populate all fields
        var status = findFields(form, payload.fields);
        if (status) return alert(status);
        var action = data.action ? data.action.toLowerCase() : "all"; // all 바꿔야 하는 부분
        Mica.require('dataSet').request(action, payload);
        afterSubmit(data);
    };

    api.getFormData = function () {
        var payload = {
            name: 'Form Data',
            test: Mica.env(),
            fields: {}
        };
        var data = {};
        var status = {};
        if ($forms && $forms.length > 0) {
            //data = $.data($forms[0], namespace);
            data.form = $("#searchForm");  // 2015-06-15 삼성 디스플레이 용.
            status = findFields(data.form, payload.fields);
        }
        return payload;
    };

    api.setFormData = function (form, data) {
        var formEL = $("#" + form);

        formEL.find(":input:not([type='submit'])").each(function (i, el) {
            var $inputEL = $(el);
            var value = data[$inputEL.attr('id')];
            if (!value) {
                $inputEL.val("");
            } else {
                switch ($inputEL.attr("input-type")) {
                    case "datepicker":
                        $.getScript('/Content/theme/js/plugins/daterangepicker/moment.min.js', function () {
                            var dateFormat = $inputEL.attr('data-format') ? $inputEL.attr('data-format').toUpperCase().replace(/YY/, 'YYYY') : "YYYY-MM-DD";
                            moment(value, ["YYYY-MM-DD", "YYYY-MM", "YYYYMM"]).isValid() ? $inputEL.val(moment(value, ["YYYY-MM-DD", "YYYY-MM", "YYYYMM"]).format(dateFormat)) : $inputEL.val(value);
                        });
                        break;

                    default:
                        $inputEL.val(value);
                        break;
                }
            }
        });
        $("select", formEL).each(function (i, el) {
            var $selectEL = $(el);
            var value = data[$selectEL.attr('id')];
            if (!value) {
                $selectEL.val("");
            } else {
                $selectEL.val(null).trigger("change");
                $selectEL.val(value).trigger("change");
                //switch ($selectEL.attr("input-type")) {
                //    case "datepicker":
                //        break;

                //    default:
                //        $selectEL.val(value);
                //        break;
                //}
            }
        });
    };


    // Common callback which runs after all Ajax submissions
    function afterSubmit(data) {
        // Reset data and enable submit button
        reset(data);
    }

    function preventDefault(data) {
        data.evt && data.evt.preventDefault();
        data.evt = null;
    }

    var disconnected = _.debounce(function () {
        alert('error');
    }, 100);

    // Export module
    return api;
});

Mica.define('splitter', function ($, _) {
    //'use strict';

    var api = {};
    var $doc = $(document);
    var $splitter;
    var loc = window.location;
    var retro = window.XDomainRequest && !window.atob;
    var namespace = 'div[splitter]';
    var siteId;
    var alert = window.alert;
    var listening;

    api.ready = function () {
        init();
        // Wire document events once
        //if (!listening) addListeners();
    };

    // 디자인이나 프리뷰시 보일껀지
    //api.preview = api.design = function () {
    //       init();
    //   };

    function init() {
        $splitter = $(namespace);
        if (!$splitter.length) return;
        $splitter.each(build);
    }

    function build(i, splitter) {
        // Store form state using namespace
        var splitterEL = $(splitter);
        //var data = $.data(splitter, namespace);
        //if (!data) data = $.data(splitter, namespace, { form: formEL }); // data.form

        //var splittertype = formEL.attr('splitter');
        for (var i = 0; i < splitterEL.length; i++) {
            var el = splitterEL[i];
            var splittertype = $(el).attr("splitter");
            if (splittertype != "col") {
                var test = $(el).find(".w-widget-jqgrid");
                test.removeClass('w-widget-jqgrid');
                var height = null;
                var width = $(el).width();
                test.addClass('w-widget-jqgrid');

                var cssName = $("html").attr("data-site");
                var classNames = $(el).attr("class").split(" ");
                var className = ""; //클래스명이 붙음.

                for (var i = 0; i < classNames.length; i++) {
                    className += "." + classNames[i];
                }
                height = micaCommon.fncS.cssStyleGet(cssName, className, "height") || null;
                className = "";
                if (height == null) {
                    for (var i = 0; i < classNames.length; i++) {
                        if (classNames[i] != "") {
                            if (height == null) {
                                height = micaCommon.fncS.cssStyleGet(cssName, "." + classNames[i], "height") || null;
                            }
                        }
                        //className += "." + classNames[i];
                    }
                }
                height = height || $(el).height();

                if (height < 2) {
                    height = "100%";
                }

                var children = $(el).children();
                var childrenWidth = [];
                var childrenMin = [];
                for (var j = 0; j < children.length; j++) {
                    childrenWidth[j] = Number($(children).attr("class").replace(/w-col w-col-/gi, "")) / 12 * 100;
                    childrenWidth[j] -= 1;
                    if (splittertype == "horizontal") {
                        childrenMin[j] = Number($(children[j]).css("min-height").replace("px", ""));
                    } else {
                        childrenMin[j] = Number($(children[j]).css("min-width").replace("px", ""));
                    }
                }

                $(el).jqxSplitter({ height: height, width: "100%", orientation: splittertype, panels: [{ size: childrenWidth[0] + "%", min: childrenMin[0] }, { size: childrenWidth[1] + "%", min: childrenMin[1] }] });

                //$(el).find(".jqx-fill-state-pressed").unbind("click");
                //$(el).find(".jqx-fill-state-pressed").bind("mouseover", function (e) {
                //    e.preventDefault();
                //    e.stopPropagation();
                //    $(this).hide();
                //});
                //$(el).find(".jqx-fill-state-pressed").parent().bind("mouseout", function (e) {
                //    e.preventDefault();
                //    e.stopPropagation();
                //    $(this).children().show();
                //});
                $(el).find(".w-col:not(.jqx-widget-content)").height("100%");
                $(el).find(".w-row:not(.jqx-widget-content)").height("100%");


                $(el).css("width", "");
                $(el).on("resize", function (event) {
                    //var childSplitters = $(this).find("[splitter=horizontal],[splitter=vertical]");
                    //for (var i = 0; i < childSplitters.length; i++) {
                    //    $(childSplitters[i])
                    //}
                    console.log("resize");
                    var splitters = $(this).find(".w-row[splitter]");
                    for (var i = 0; i < splitters.length; i++) {
                        var splitter = $(splitters[i]);
                        var splitterHeight = splitter.parent().height();
                        if ($(splitter).parents("[splitter]").length > 0 && $(splitter).parent().next().length < 1) {
                            if (splitter.parent().attr("style").indexOf("height") < 0) {
                                splitterHeight -= 8;
                            }
                            //|| $(splitter).parent().next().css("cursor")
                        }
                        //$(splitter).parent().next().hasClass("jqx-splitter-splitbar-horizontal")

                        splitter.height(splitterHeight);
                    }

                    var gridTables = $(".jqx-splitter .ui-jqgrid.ui-widget.ui-widget-content");
                    var grids = $(".jqx-splitter .w-widget.w-widget-jqgrid");
                    for (var i = 0; i < grids.length; i++) {
                        var grid = $(grids[i]);
                        grid.css("min-height", 0);
                        grid.height("");
                        grid.parent().height("");
                        if (grid.parents(".modal").length > 0) {

                        } else if (grid.closest('[splitter="col"]').length > 0) {
                            var rowCol = grid.closest('[splitter="col"]');
                            if (rowCol.find(".w-col").length > 0) {
                                //                                rowCol = grid.closest('.w-col');
                                rowCol = grid.closest('.w-col.jqx-splitter-panel');
                            }

                            var height1 = rowCol.height();
                            var height2 = rowCol.children().height();

                            var heightTmp = 0;
                            var children = null;
                            if (height1 - height2 < 5) {
                                children = rowCol.children().children();
                                if (height1 - height2 == 0) {
                                    children = grid.closest('.w-col');
                                }
                            } else {
                                children = rowCol.children();
                            }
                            if (children[0].tagName.toLowerCase() == "ul" || children[0].tagName.toLowerCase() == "li") {
                                //                                children = $(children.find("li")[0]).children();
                                children = grid.closest("li").children();
                            }
                            heightTmp = childrenHeight(children, null, "w-widget");

                            var width = grid.parent().width();
                            var height = grid.parent().height();

                            if (grid.closest("ul").children().length > 0) {
                                var count = grid.closest("ul").children().length + 2;
                                var tmp = heightTmp - height;
                                height = grid.closest("[splitter=col]").height() / count;
                                if (grid.prev().hasClass("w-widget-chart")) {
                                    height = height * 3;
                                }
                                heightTmp = height + tmp;
                                console.log("height : " + height + ", tmp : " + heightTmp);
                            }

                            grid.parent().height(height - (heightTmp - height) - 6); // widget jqgrid;
                            if (heightTmp != 0) {
                                height = height - (heightTmp - height);
                                if (grid.prev().hasClass("w-widget-chart")) {
                                    height = height / 2;
                                }
                                height -= grid.find("thead").height() + grid.find(".ui-jqgrid-pager").height();
                            }
                            height -= grid.find(".ui-jqgrid-pager").css("padding-top") ? Number(grid.find(".ui-jqgrid-pager").css("padding-top").replace("px", "")) : 0;
                            gridTables = grid.find(".ui-jqgrid.ui-widget.ui-widget-content");
                            if (gridTables.length > 0) {
                                //                            	&& grid.closest(".modal").length < 1
                                var gridTable = $(gridTables).find("table.ui-jqgrid-btable.ui-common-table");
                                for (var j = 0; j < gridTable.length; j++) {
                                    if ($(gridTable[j]).closest(".modal").length < 1) {
                                        $(gridTable[j]).setGridWidth(width - 1);
                                        if (grid.closest("ul").children().length > 0) { height -= 3; }
                                        grid.height(height);
                                        $(gridTable[j]).setGridHeight(height - 10);
                                    }
                                }
                                //                            	$(gridTables).find("table.ui-jqgrid-btable.ui-common-table").setGridWidth(width-1);
                                //                        		$(gridTables).find("table.ui-jqgrid-btable.ui-common-table").setGridHeight(height - 5);
                            }
                            //                            if (gridTables[i] != null) {
                            //                                $(gridTables[i]).find("table.ui-jqgrid-btable.ui-common-table").setGridWidth(width-1);
                            //                                $(gridTables[i]).find("table.ui-jqgrid-btable.ui-common-table").setGridHeight(height - 5);
                            //                            }
                        }
                        //grid.find("table.ui-jqgrid-btable.ui-common-table").setGridWidth(width);
                    }
                    //var charts = Mica.require("dataSet").charts;
                    //var charts = $(".amcharts-main-div").parent();
                    var charts = $(".w-widget.w-widget-chart");
                    for (var i = 0; i < charts.length; i++) {
                        var chart = $(charts[i]);
                        chart.parent().height("");
                        chart.height("");

                        if (chart.closest('[splitter="col"]').length > 0) {
                            var rowCol = chart.closest('[splitter="col"]');
                            if (rowCol.find(".w-col").length > 0) {
                                //                                rowCol = chart.closest('.w-col');
                                rowCol = chart.closest(".w-col.jqx-splitter-panel");
                            }

                            var height1 = rowCol.height();
                            var height2 = rowCol.children().height();

                            var heightTmp = 0;
                            var children = null;
                            var chartParent = chart.parent();
                            if (height1 - height2 < 5) {
                                children = rowCol.children().children();
                                if (height1 - height2 == 0) {
                                    children = chart.closest('.w-col');
                                }
                            } else {
                                children = rowCol.children();
                            }

                            if (chartParent[0].tagName.toLowerCase() == "ul" || chartParent[0].tagName.toLowerCase() == "li") {
                                chartParent.height("100%");
                                chartParent.parent().height("100%");
                            }
                            if (chartParent.closest(".content").length > 0) {
                                chartParent = chartParent.closest(".content");
                            }

                            if (children[0].tagName.toLowerCase() == "ul" || children[0].tagName.toLowerCase() == "li") {
                                //                                children = $(children.find("li")[0]).children();
                                children = chart.closest("li").children();
                            }
                            chartParent.height("100%");

                            heightTmp = childrenHeight(children, null, "w-widget");
                            //                            if (children.find(".measure-box").length < 1) {
                            //                                heightTmp = childrenHeight(children.closest(".measure-box").children(), null, "w-widget");
                            //                            }
                            //chart.parent().height("100%");

                            var width = chart.parent().width();
                            var height = chartParent.height();
                            //                            var height = chart.parent().height();
                            if (chart.closest("ul").children().length > 0) {

                                var cssWidth = micaCommon.fncS.cssStyleGet($("html").attr("data-site"), "." + chart.closest("li").attr("class"), "width") || "";
                                if (cssWidth.indexOf("%") < 0) {
                                    var count = chart.closest("ul").children().length + 2;
                                    var tmp = heightTmp - height;
                                    height = chart.closest("[splitter=col]").height() / count;
                                    if (chart.next().hasClass("w-widget-jqgrid")) {
                                        height = height * 3;
                                    }
                                    heightTmp = height + tmp;
                                }
                            }

                            //                            chart.parent().height("");
                            chartParent.height("");
                            if (heightTmp != 0) {
                                height = height - (heightTmp - height);
                            }

                            //                            chart.parent().height(height - 6); // widget jqgrid;

                            chartParent.height(height - 6);
                            if (chart.next().hasClass("w-widget-jqgrid")) {
                                //                                height -= chart.next().height();
                                //                            	height -= 95;
                                //                                chart.parent().height(height - 6); // widget jqgrid;
                                height = height / 2;
                            }
                            chart.height(height - 10);
                            if (!chart.children().hasClass("amcharts-main-div")) {
                                chart.children().height("100%");
                            }
                            //                            chart.find(".amcharts-main-div,.amcharts-chart-div").height("100%");
                            chart.find(".amcharts-main-div").height("100%");

                        }
                    }

                    //                    var contents = $(".jqx-splitter .content");
                    //                    for (var i = 0; i < contents.length; i++) {
                    //                        if ($(contents[i]).children(".w-widget").length < 1) {
                    //
                    //                        }
                    //                    }

                    //var chart = charts[i];
                    //chart.invalidateSize();
                });
                //$(window).on("resize", function(){
                //    //$(el).trigger("resize");
                //});

                function childrenHeight(el, height, classCheck) {
                    el = $(el);
                    height = height || 0;

                    if (el.length > 1) {
                        for (var i = 0; i < el.length; i++) {
                            var eli = $(el[i]);
                            if (eli.css("float") == null || eli.css("float") == "none") {
                                height += eli.height();
                            }
                        }
                    } else if (el.hasClass(classCheck)) {
                        return el.closest("[splitter=col]").height();
                    } else {
                        if (el.length == 0) {
                            return 0;
                        }
                        return childrenHeight(el.children(), height, classCheck);
                    }
                    return height;
                }
            }
        }
        //data.handler = null;
        //data.redirect = formEL.attr('data-redirect');

        //        $("select", formEL).each(function (i, el) {
        //            var $selectEL = $(el);
        //            $selectEL.removeClass("w-select");

        //            var dataSetID = $selectEL.attr("data-dynamic");
        //            var onClick = $selectEL.attr("data-request");

        //            // multiple 임시 적용
        //            if ($selectEL.attr("input-type") == "multiple") {
        //                $selectEL.prop("multiple", true);
        //                $selectEL.select2();
        //                /* 멀티 시 한줄에 표시할 수 있씀
        //                $.getScript('/theme/js/plugins/bootstrap-multiselect/bootstrap-multiselect.js', function () {
        //                    $selectEL.multiselect({
        //                        inheritClass: true,
        ////                        numberDisplayed: 4,
        //                        buttonClass: 'btn btn-default btn-multi',
        //                        includeSelectAllOption: true,
        //                        enableFiltering: true
        //                    });
        //                });
        //*/
        //            }
        //            else {
        //                if (dataSetID != null) {
        //                    $selectEL.html("");
        //                    $selectEL.select2({
        //                        placeholder: "",
        //                        allowClear: true
        //                    });

        //                    if (onClick == 'y') {
        //                        $selectEL.select2().on("select2-opening", function (e) {
        //                            // Avoid late clicks on touch devices
        //                            if (!Mica.validClick(e.currentTarget)) return;
        //                            $.ajaxSetup({ aync: false });
        //                            selectOptionFnc($selectEL, dataSetID, true);
        //                            $.ajaxSetup({ aync: true });
        //                        });
        //                    }
        //                    else {
        //                        setTimeout(function () {
        //                            selectOptionFnc($selectEL, dataSetID, false);
        //                        }, 500);
        //                    }
        //                }
        //                else {
        //                    $selectEL.select2();
        //                }
        //            }
        //        });
        //$(".select2-arrow").hide();
        //data.handler = submitForm; return;
    }
    return api;
});
Mica.define('fileInput', function ($, _) {
    //'use strict';

    var api = {};
    var $doc = $(document);
    var $splitter;
    var loc = window.location;
    var retro = window.XDomainRequest && !window.atob;
    var namespace = 'div.w-widget-fileinput';
    var siteId;
    var alert = window.alert;
    var listening;

    api.ready = function () {
        init();
    };


    function init() {
        $splitter = $(namespace);
        if (!$splitter.length) return;
        $splitter.each(build);
    }

    function build(i, splitter) {
        // Store form state using namespace
        var splitterEL = $(splitter);
        var bindList = micaCommon.bindSetList();
        var bindInfo = bindList[splitter.id];

        var optionScript = bindInfo.optionScript || "{}";
        optionScript = JSON.parse(optionScript);
        var success = optionScript.success == null ? null : new Function(["data", "textStatus", "jqXHR"], optionScript.success);
        var error = optionScript.error == null ? null : new Function(["jqXHR", "textStatus", "errorThrown"], optionScript.error);
        var url = optionScript.url;

        var options = {
            url: url,
            success: success,
            error: error
        };
        micaCommon.fileInput.set(splitterEL, options);
    }
    return api;
});

/**
 * ----------------------------------------------------------------------
 * Mica: Interactions
 */
Mica.define('dataSet', function ($, _) {
    //'use strict';

    var api = {};
    var dataSetList = {};
    var bindList = {}; //바인딩 정보 리스트
    var bindTable = {};// 데이터셋별 바인딩 정보 테이블

    // -----------------------------------
    // 바인딩 정보 초기화
    api.initBind = function (list) {
        bindList = {};
        _.each(list, function (item) {
            bindList[item.id] = item;
            if (bindTable[item.dataSetId] == undefined)
                bindTable[item.dataSetId] = [];
            bindTable[item.dataSetId][bindTable[item.dataSetId].length] = item.id;
        });
    };
    //데이터 셋 초기화 및
    var isReload = false;
    api.dataSet = function (list) {
        if (!list) return;

        dataSetList = {};
        _.each(list, function (item) {
            var el = $("#" + item.id);
            //dataSetList[item.id] = item;
            if (el && el.css('display') != "none") {
                if (bindTable[item.id]) {
                    item.count = 0;
                    item.success = false;
                    item.result = [];
                    dataSetList[item.id] = item;
                    // 데이터 리로딩 처리
                    //isReload && item.refreshCycle && item.refreshCycle != 0 && typeof parseInt(item.refreshCycle) === "number" &&
                    //setInterval("Mica.require('dataSet').request('" + item.id + "')", parseInt(item.refreshCycle) * 1000 * 10);
                    if (item.refreshCycle) {
                        try {

                            if (Number(item.refreshCycle)) {
                                setInterval(function () {
                                    Mica.require('dataSet').request(item.id);
                                }, parseInt(item.refreshCycle) * 1000 * 60);

                            }
                        } catch (e) {

                        }
                    }
                }
            }
        });
    };

    api.reload = function (dataSetID, timer, flag) {
        if (flag) {
            typeof parseInt(timer) === "number" && setInterval("Mica.require('dataSet').request('" + dataSetID + "')", parseInt(timer) * 1000);
        }
    };

    api.requestByID = function (id) {
        if (!id) return;
        var dataSetID = bindList[id].dataSetId;

        var payload = Mica.require('forms').getFormData();
        payload && changParam(dataSetList[dataSetID], payload);
        build(dataSetList[dataSetID], dataSetID);
    };

    api.request = function (dataSetID, params) {
        if (!dataSetID) return;
        var payload = params;
        payload || (payload = Mica.require('forms').getFormData());

        if (dataSetID == "all") {
            _.each(dataSetList, function (dataSet) {
                if (payload) changParam(dataSet, payload);
                build(dataSet, dataSet.id);
            });
        }
        else {
            payload && changParam(dataSetList[dataSetID], payload);
            return build(dataSetList[dataSetID], dataSetID);
        }
    };

    api.requestData = function (dataSetID, params) {
        if (!dataSetID) return false;
        var payload = params;
        payload || (payload = Mica.require('forms').getFormData());
        payload && changParam(dataSetList[dataSetID], payload);
        return build(dataSetList[dataSetID], dataSetID, true);  // return data is true;
    };

    api.dataOK = function (dataSetID) {
        //데이터 확인 sjjo
        if (dataSetID) return dataSetList[dataSetID];
        else return dataSetList;
    };

    api.bindList = function () {
        return bindList;
    };

    api.dataSetReload = function (dataSetID) {
        if (dataSetID) {
            _.each(bindTable[dataSetID], function (bindID) {
                completeBind(dataSetID, bindID); // 가져온 데이터로 바인딩하기
            });
        } else {
            $.each(bindTable, function (dataSetID, bindID) { //dataSetID , bindID : div id
                $.each(bindID, function (i, value) {
                    completeBind(dataSetID, value); // 가져온 데이터로 바인딩하기
                });
            });
            //_.each(bindTable, function (bindID) {
            //    completeBind(dataSetID, bindID); // 가져온 데이터로 바인딩하기
            //});
        }
    };
    api.dataSetList = function () {
        return dataSetList;
    };
    //api.bindList = function () {
    //    return bindList;
    //}

    function changParam(dataSet, payload) {
        if (!dataSet || !dataSet.agentParameter) return;

        _.each(payload.fields, function (field) {
            _.each(dataSet.agentParameter, function (param) {
                if (param.name == field.name) param.value = field.value;
            });
        });
    }

    function build(dataSet, dataSetID, returnData) {
        //var loading = '<div id="splash" style="float: left; position: absolute; z-index: 9999; top: 0px; width: 99%; height: 99%; background-color: white; opacity: 0.6; display: none;">'
        //        + '<img alt="" src="/Content/theme/img/loading.gif" style="position: absolute; border: none; left: ' + ($(window).width() / 2 - 230) + 'px; top:' + ($(window).height() / 2 - 200) + 'px">'
        //        + '</div>';
        //!$("#splash").length && $('body').append(loading).click(function () { $("#splash").remove(); });

        var loading = '<div id="splash" style="float: left; position: absolute; z-index: 999; top: 0px; width: 99%; height: 99%; background-color: white; opacity: 0.6; display: none;">'
               + '<img alt="" src="/Content/theme/img/loading.gif" style="position: absolute; border: none; left: ' + ($(window).width() / 2 - 230) + 'px; top:' + ($(window).height() / 2 - 200) + 'px">'
               + '</div>';
        !$("#splash").length && $('body').append(loading).click(function () { $("#splash").hide(); });


        var agentIdType = dataSet.agentIdType;
        var prm = {};
        // DATABASE
        switch (agentIdType) {
            case "D":
                prm.Type = "J";
                break;
            default:
                prm.Type = "D";
                break;
        }
        prm.GUBUN = agentIdType;
        if (typeof dataStop != "undefined") {
            if (dataStop[dataSet.agentId]) {
                return;
            }
        }

        var data = dataSetList[dataSetID].cacheCycle == null || dataSetList[dataSetID].cacheCycle == "0" ? null : lscache.get(dataSet.agentId + JSONtoString(arrayParamsFix(dataSet.agentParameter)));
        //var data = null; // lscache 적용
        if (data) {
            if (data.success) {
                dataSetList[dataSetID].success = data.success;
                "string" == typeof data.result && (data.result = JSON.parse(data.result));
                dataSetList[dataSetID].result = data.result;
                //dataSetList[dataSetID].result = $.extend(data.result, data.result["rows"])

                if (returnData) return data;

                !returnData && bindTable[dataSetID] &&
                _.each(bindTable[dataSetID], function (bindID) {
                    completeBind(dataSetID, bindID); // 가져온 데이터로 바인딩하기
                });
            } else {
                console.error('Failed to load content! ID: %o, Error Msg: %o', dataSet.agentId, data.errMsg);
                return false;
            }
        } else {
            return fetchContent(dataSet, prm).then(function (data) {
                if (data.success) {
                    dataSetList[dataSetID].success = data.success;
                    //dataSetList[dataSetID].result = data.result;
                    dataSetList[dataSetID].result = $.extend(data.result, data.result["rows"]);

                    if (returnData) return data;

                    bindTable[dataSetID] &&
                    _.each(bindTable[dataSetID], function (bindID) {
                        completeBind(dataSetID, bindID); // 가져온 데이터로 바인딩하기
                    });
                } else {
                    console.error('Failed to load content! ID: %o, Error Msg: %o', dataSet.agentId, data.errMsg);
                    return false;
                }
            });
        }
    }

    function completeBind(dataSetID, bindID) {
        var bindInfo = bindList[bindID] ? bindList[bindID] : undefined;
        if (!bindInfo) return;

        var type = bindInfo.type ? bindInfo.type : "form";
        switch (type) {
            case "chart":
                amChartSet(bindInfo, dataSetID);
                break;
            case "jqgrid":
                jqGridSet(bindInfo, dataSetID);
                break;
            case "form":
                formBindSet(bindInfo, dataSetID);
                break;
            case "d3chart":
                d3ChartSet(bindInfo, dataSetID);
                break;
            case "jqxtree":
                jqxtreeSet(bindInfo, dataSetID);
                break;
            case "jqxgrid":
                jqxgridSet(bindInfo, dataSetID);
                break;
            case "jqxmenu":
                jqxmenuSet(bindInfo, dataSetID);
                break;
            case "checkboxdata":
                checkboxdataSet(bindInfo, dataSetID);
                break;
            case "radiodata":
                radiodataSet(bindInfo, dataSetID);
                break;
        }
    }

    function arrayParamsFix(params) {
        var cond = {};
        if (params.count < 1) return cond;
        _.each(params, function (param, key) {
            cond[param.name] = (param.value === undefined) ? "" : param.value;
        });
        return cond;
    }

    //prm.page = 1;
    //prm.rows = 10;
    //prm.sidx = "";
    //prm.sord = "";
    function fetchContent(dataSet, prm) {
        var params = dataSet.agentParameter;
        params = arrayParamsFix(dataSet.agentParameter);

        var apiUrl = Mica.agentURL + "/Agent/GetComponentInfo?id=" + dataSet.agentId + "&GUBUN=" + prm.GUBUN + "&Type=" + prm.Type + "&page=" + 1 + "&rows=" + 0;
        if (dataSet.agentIdType == "R" || dataSet.agentIdType == "F") {  // Remote Type 처리
            var paramsObj = {};
            _.each(dataSet.agentParameter, function (item) {
                item.value === undefined && (item.value = "");
                switch (item.type) {
                    case "B":
                        paramsObj[item.name] = Boolean(item.value);
                        break;
                    case "N":
                        paramsObj[item.name] = Number(item.value);
                        break;
                    default:
                        paramsObj[item.name] = item.value;
                        break;
                }
            });

            if (dataSet.paramObject && dataSet.paramsObjectName != undefined && dataSet.paramsObjectName != "") {
                var paramObject = JSON.parse(dataSet.paramObject);
                paramObject[dataSet.paramsObjectName] = paramsObj;
                paramsObj = paramObject;
            } else if (dataSet.paramObject != undefined) {
                paramsObj = dataSet.paramObject;
            }

            $('#splash').show();
            if (paramsObj.WorkType == null) paramsObj = JSON.stringify(paramsObj);  // .net, spring 파라미터 처리 구분
            if (paramsObj == "{}" || paramsObj == '{"undefined":""}') {

                // Restful get 방식 호출시 query string 을 사용자가 입력한 값으로 대체
                if (dataSet.agentIdType == "F") {
                    var apiUrl = dataSet.url;
                    if (apiUrl.split('?').length > 1) {
                        var apiParams = $.parseParams(apiUrl.split('?')[1]);
                        var formValues = $('form').serializeArray();
                        var parsedQueryString = "";
                        $.each(apiParams, function (key, queryValue) {
                            //alert(key);
                            var tmpValue = "";
                            $.each(formValues, function (i, obj) {
                                if (obj.name == key && '' != obj.value) {
                                    queryValue = obj.value; //대체
                                    return false;
                                }
                            });
                            parsedQueryString = parsedQueryString + "&" + key + "=" + queryValue;
                        });

                        apiUrl = apiUrl.split('?')[0];
                        if ('' != parsedQueryString) {
                            apiUrl = apiUrl + '?' + parsedQueryString.substring(1);
                        }
                    }

                    dataSet.url = apiUrl;
                }

                return $.get(dataSet.url).done(function () {
                    $('#splash').hide();
                }).then(function (data, status, headers) {
                    var retouch = { result: {}, success: false };
                    "string" == typeof data && (data = JSON.parse(data));
                    if (dataSet.objectName) {
                        if (data[dataSet.objectName] == null) {

                            var value = eval("data");  // return 변수를 retouch
                            if (value) {
                                retouch.result = value;
                                retouch.success = true;
                            }
                        } else {
                            var value = eval("data." + dataSet.objectName);  // return 변수를 retouch
                            if (value) {
                                retouch.result["rows"] = value;
                                retouch.success = true;
                            }
                        }
                    }
                    else if (dataSet.dataObjectName) {
                        //value = data[dataSet.dataObjectName];
                        retouch.result["rows"] = micaCommon.fncS.hierarchyReturn(data, dataSet.dataObjectName);
                        retouch.success = true;
                    } else {
                        retouch.result["rows"] = data;
                        retouch.success = true;
                    }
                    var value = {};

                    return retouch;
                }, function (data, status, headers) {
                    $('#splash').hide();
                    console.error('Failed to load content! Data: %o, Status: %o, Headers: %o', data, status, headers);
                });
            } else {
                if (dataSet.agentIdType == "F") {

                    //return ajaxService(paramsObj, dataSet.url).done(function () {
                    ajaxService.clearAuth();
                    var contentType = "application/json; charset=utf-8";
                    $.ajaxSetup({
                        contentType: contentType
                    });
                    if (dataSet.methodType) {
                        switch (dataSet.methodType.toLowerCase()) {
                            case "get": //호출되지 않는 로직이라고 판단됨.. bingxu. 2016.04.22
                                return $.get(dataSet.url, paramsObj).done(function () {
                                    $('#splash').hide();
                                }).then(function (data, status, headers) {
                                    var retouch = { result: {}, success: false };
                                    "string" == typeof data && (data = JSON.parse(data));
                                    //if (dataSet.objectName) {
                                    //var value = eval("data." + dataSet.objectName);  // return 변수를 retouch
                                    //var value = micaCommon.restFul.mapping(data, dataSet.colModelsName, dataSet.dataObjectName);
                                    var value = null;
                                    if (dataSet.colModelsName) {
                                        value = micaCommon.restFul.mapping(data, dataSet.colModelsName, dataSet.dataObjectName);
                                    } else {
                                        if (dataSet.dataObjectName) {
                                            //value = data[dataSet.dataObjectName];
                                            value = micaCommon.fncS.hierarchyReturn(data, dataSet.dataObjectName);
                                        } else {
                                            value = data;
                                        }
                                    }
                                    if (value) {
                                        retouch.result["rows"] = value;
                                        retouch.success = true;
                                    }
                                    //}
                                    //data.result.rows ? (retouch.result.rows.length < 10000) && lscache.set(dataSet.agentId + JSONtoString(params), retouch, dataSet.cacheCycle) : lscache.set(dataSet.agentId + JSONtoString(params), retouch, dataSet.cacheCycle);  //caching  cacheCycle

                                    return retouch;
                                }, function (data, status, headers) {
                                    $('#splash').hide();
                                    console.error('Failed to load content! Data: %o, Status: %o, Headers: %o', data, status, headers);
                                });
                                break;
                            case "post":
                            case "put":
                            case "delete":
                                return $.ajax({ url: dataSet.url, data: paramsObj, method: dataSet.methodType.toLowerCase() }).done(function () {
                                    $('#splash').hide();
                                }).then(function (data, status, headers) {
                                    var retouch = { result: {}, success: false };
                                    "string" == typeof data && (data = JSON.parse(data));
                                    //if (dataSet.objectName) {
                                    //var value = eval("data." + dataSet.objectName);  // return 변수를 retouch
                                    //var value = micaCommon.restFul.mapping(data, dataSet.colModelsName, dataSet.dataObjectName);
                                    var value = null;
                                    if (dataSet.colModelsName) {
                                        value = micaCommon.restFul.mapping(data, dataSet.colModelsName, dataSet.dataObjectName);
                                    } else {
                                        if (dataSet.dataObjectName) {
                                            //value = data[dataSet.dataObjectName];
                                            value = micaCommon.fncS.hierarchyReturn(data, dataSet.dataObjectName);
                                        } else {
                                            value = data;
                                        }
                                    }
                                    if (value) {
                                        retouch.result["rows"] = value;
                                        retouch.success = true;
                                    }
                                    //}
                                    //data.result.rows ? (retouch.result.rows.length < 10000) && lscache.set(dataSet.agentId + JSONtoString(params), retouch, dataSet.cacheCycle) : lscache.set(dataSet.agentId + JSONtoString(params), retouch, dataSet.cacheCycle);  //caching  cacheCycle

                                    return retouch;
                                }, function (data, status, headers) {
                                    $('#splash').hide();
                                    console.error('Failed to load content! Data: %o, Status: %o, Headers: %o', data, status, headers);
                                });
                                break;
                        }
                    }

                } else {
                    return $.post(dataSet.url, paramsObj).done(function () {
                        $('#splash').hide();
                    }).then(function (data, status, headers) {
                        var retouch = { result: {}, success: false };
                        "string" == typeof data && (data = JSON.parse(data));
                        if (dataSet.objectName) {
                            var value = eval("data." + dataSet.objectName);  // return 변수를 retouch
                            if (value) {
                                retouch.result["rows"] = value;
                                retouch.success = true;
                            }
                        }
                        //data.result.rows ? (retouch.result.rows.length < 10000) && lscache.set(dataSet.agentId + JSONtoString(params), retouch, dataSet.cacheCycle) : lscache.set(dataSet.agentId + JSONtoString(params), retouch, dataSet.cacheCycle);  //caching  cacheCycle

                        return retouch;
                    }, function (data, status, headers) {
                        $('#splash').hide();
                        console.error('Failed to load content! Data: %o, Status: %o, Headers: %o', data, status, headers);
                    });
                }
            }
        } else {
            return $.post(apiUrl, params).done(function () { $('#splash').hide(); }).then(function (data, status, headers) {
                // object / string 확인 필요 by.joon
                //lscache.set(dataSet.agentId + JSONtoString(params), data, 5);  //caching  by 5 min
                "string" == typeof data && (data = JSON.parse(data));
                if (data.success) {
                    "string" == typeof data.result && (data.result = JSON.parse(data.result));
                }
                // lscache 적용
                data.result.rows ? (data.result.rows.length < 10000) && lscache.set(dataSet.agentId + JSONtoString(params), data, dataSet.cacheCycle) : lscache.set(dataSet.agentId + JSONtoString(params), data, dataSet.cacheCycle);  //caching  cacheCycle
                return data;
            }, function (data, status, headers) {
                $('#splash').hide();
                console.error('Failed to load content! Data: %o, Status: %o, Headers: %o', data, status, headers);
            });
        }
    }

    function formBindSet(bindInfo, dataSetID) {
        if (bindInfo.children.length < 1 || !dataSetList[dataSetID]) return;
        if (!dataSetList[dataSetID].result) {
            console.error('Failed to bind content! dataSetID: %o', dataSetID);
            return;
        }

        var data = dataSetList[dataSetID].result;
        if (dataSetList[dataSetID].agentIdType === "S") {
            data = dataSetList[dataSetID].result[0];
        } else if (dataSetList[dataSetID].agentIdType === "R" && dataSetList[dataSetID].singleData == "y") {
            data = dataSetList[dataSetID].result.rows[0];
        } else if (dataSetList[dataSetID].agentIdType === "L" && dataSetList[dataSetID].singleData == "y") {
            data = dataSetList[dataSetID].result.rows;
        } else if (dataSetList[dataSetID].singleData == "y") {
            data.rows = dataSetList[dataSetID].result.rows;
        }

        _.each(bindInfo.children, function (item) {
            var value = data[item.bindId];
            var el = $("#" + item.id);
            if (value && el) {
                switch (item.tag) {
                    case "select":
                        var optionTag = '<option value="#{value}">#{text}</option>';
                        var optionTagSet = "";
                        //$("#" + item.id).empty();

                        $(el).html("");
                        var num = $(el).jqxComboBox("getItems").length;
                        for (var i = 0; i < num; i++) {
                            $(el).jqxComboBox("removeAt", 0);
                        }

                        if (typeof value == "object") {
                            for (var ii in value) {
                                var selectvalue = value[ii].VALUE || value[ii].value;
                                var selecttext = value[ii].TEXT || value[ii].text;
                                var optionTagSetTmp = optionTag.replace(/#{value\}/gi, selectvalue);
                                optionTagSet += optionTagSetTmp.replace(/#{text\}/gi, selecttext);
                                $(el).jqxComboBox("addItem", { label: selecttext, value: selectvalue });
                            }
                        } else {
                            optionTagSet = optionTag.replace(/\{value\}/g, value);
                        }
                        el.html(optionTagSet);
                        //$(el).select2();
                        break;
                    case "input":
                        if (el.attr('input-type') == "datepicker") {
                            $.getScript('/Content/theme/js/plugins/daterangepicker/moment.min.js', function () {

                                var date_format = el.attr('data-format') ? el.attr('data-format').toUpperCase().replace(/YY/, 'YYYY') : "YYYY-MM-DD";
                                if (value == "#startOfWeek") value = moment().startOf('week').format(date_format);
                                else if (value == "#startOfMonth") value = moment().startOf('month').format(date_format);
                                else if (value == "#thisMonth") value = moment().format(date_format);
                                else if (value == "#lastMonth") value = moment().month(-1).format(date_format);
                                else if (value == "#today") value = moment().format(date_format);
                                else if (value == "#yesterday") value = moment().subtract(1, 'day').format(date_format);
                                el.val(value);
                                el.text(value);
                            });
                        } else {
                            el.val(value);
                            el.text(value);
                        }
                        break;
                    case "label":
                        el.val(value);
                        el.text(value);
                        break;
                    case "div":
                        el.val(value);
                        el.text(value);
                        break;
                    case "ul":
                        el.html("");
                        var liStr = "";
                        for (var i in value) {
                            var valueData = value[i];
                            if (valueData["condition"] != null) {
                                liStr += "<li class= 'con_info_list list-dataset " + valueData["text"];
                                liStr += "' condition='" + JSON.stringify(valueData["condition"]) + "' style='cursor:pointer;'>";
                                liStr += valueData["text"];
                                //liStr += "<a>" + valueData["text"] + "</a>";
                            } else {
                                liStr += "<li class= 'con_rbox_item list-dataset " + valueData["text"];
                                liStr += "'>";
                                liStr += valueData["text"];
                            }
                            liStr += "</li>";
                        }
                        el.html(liStr);

                        //임시. 삼성디스플레이 조성진.
                        //$(el).find(".list-dataset[condition]").click(function () {
                        //    var condition = JSON.parse($(this).attr("condition"));
                        //    var parentName = $(this).parent().attr("name") || "";
                        //    for (var i = 0; i < condition.length; i++) {
                        //        $("[name="+parentName + condition[i].id + "]").jqxComboBox("selectItem", condition[i].value);
                        //        $("[name=" + parentName + condition[i].id + "]").jqxComboBox("uncheckAll");
                        //        for (var j = 0; j < condition[i].value.length; j++) {
                        //            $("[name=" + parentName + condition[i].id + "]").jqxComboBox("checkItem", condition[i].value[j]);
                        //        }
                        //    }
                        //    Mica.require('dataSet').request("all", {line:"line"});
                        //});
                        //break;
                }
            }
        });
    }

    api.charts = [];
    function amChartSet(bindInfo, dataSetID) {
        if (!dataSetList[dataSetID] || !dataSetList[dataSetID].result || !dataSetList[dataSetID].result.rows) {
            console.error('Failed to bind content! dataSetID: %o', dataSetID);
            return;
        }

        var data = dataSetList[dataSetID].result.rows;
        var options = JSON.parse(bindInfo.optionScript.replace(/\"/g, '"'));
        if (data) options.dataProvider = data;

        var chartType = options.type && ('/Content/amcharts/' + options.type + '.js');

        $.getScript(chartType, function () {
            api.charts.push(AmCharts.makeChart($("#" + bindInfo.id)[0], options));
            $("[splitter].jqx-widget").trigger("resize");
        });
    }


    function jqxtreeSet(bindInfo, dataSetID) {
        if (!dataSetList[dataSetID] || !dataSetList[dataSetID].result) {
            console.error('Failed to bind content! dataSetID: %o', dataSetID);
            return;
        }
        var dataList = dataSetList[dataSetID].result;
        if (typeof dataList == "string") {
            dataList = JSON.parse(dataList);
        }

        //$("#" + bindInfo.id).html("");
        var $native = $("#" + bindInfo.id);
        $native.html("");
        var w, h;
        h = $native.height();
        w = $native.width() - 17;
        if (h > 0) {
            d3ChartHeight = h;
            $native.height("100%")
        } else {
            h = d3ChartHeight;
        }
        if (h < 1) {
            h = 75;
        }

        h = w / 4;
        //var dataList = data;

        delete dataList["success"];
        delete dataList["rows"];
        for (var num in dataList) {
            var title = dataList[num].title;
            var video = dataList[num].video;
            var pco = "off", pm = "off";
            //            $($native).append("<div><span>" + title + "</span></div>");
            dataList[num].pco && (pco = "on");
            dataList[num].pm && (pm = "on");

            var treemapDiv = $($native).append("<div><div class='treemap'></div></div>");
            var data = dataList[num].list;
            var source =
            {
                datatype: "json",
                datafields: [
                    { name: 'id' },
                    { name: 'parentid' },
                    { name: 'text' },
                    { name: 'video' },
                    { name: 'color' },
                    { name: 'value' }
                ],
                id: 'id',
                localdata: data
            };

            // create data adapter.
            var dataAdapter = new $.jqx.dataAdapter(source);
            // perform Data Binding.
            dataAdapter.dataBind();
            // get the treemap sectors. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents
            // the sub items collection name. Each jqxTreeMap item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter
            // specifies the mapping between the 'text' and 'label' fields.
            var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label' }]);
            $(treemapDiv.find(".treemap")).jqxTreeMap({
                //                $($($native).children()[num]).jqxTreeMap({
                width: w,
                height: h,
                source: records,
                baseColor: '#0afaaa',
                renderCallbacks: {
                    '*': function (element, data, dd) {
                        if (!data.parent) {
                            //element.css({
                            //    backgroundColor: '#fff',
                            //    border: '1px solid #aaa'
                            //});
                            var width = element.width() - 2;
                            element.append('<span style="max-width:' + width + 'px;" class="jqx-treemap-header">' + data.label + '</span>');
                            return element;
                        }
                        else {

                            var nThreshold = 105;
                            var bgDelta = (data.rgb.r * 0.299) + (data.rgb.g * 0.587) + (data.rgb.b * 0.114);
                            var foreColor = (255 - bgDelta < nThreshold) ? 'Black' : 'White';
                            element.css({
                                "color": foreColor
                            });

                            if (true) {
                                var width = element.width() - 2;
                                element.append('<span style="max-width:' + width + 'px;" class="jqx-treemap-label">' + data.label + '</span>');
                                return element;
                            }
                        }
                    }/*,
                            'group1': function (element, data) {
                                if (!data.parent) {
                                    element.css({
                                        backgroundColor: '#fff',
                                        border: '1px solid #aaa'
                                    });
                                }
                            }*/
                }
            });
            $(treemapDiv.find(".treemap")).append("<div  class='treemap-header'><span class='w-icon fa fa-caret-right'></span> " + title + "</div>");
            $(treemapDiv.find(".treemap")).append("<div class='state-led' style=' right: 0px;'><img src='/Content/img/pco_" + pco + ".png'></div>");
            $(treemapDiv.find(".treemap")).append("<div class='state-led' style=' right:40px;'><img src='/Content/img/pm_" + pm + ".png'></div>");
            if (!video) {
                $(treemapDiv.find(".treemap")).append("<div class='video' style='transform: rotate(330deg); right: 4px; bottom: -1px; cursor:pointer;'><img src='/Content/img/video.png'></div>");
                videoFnc();
                function videoFnc() {
                    $(".video").unbind("click");
                    $(".video").click(function () {
                        //$("#aButton").trigger("click");
                        $("#aButton").click();
                        videoFnc();
                    });
                }
            }
        }

        $(window).unbind("resize");
        $(window).bind('resize', function () {

            //var $native = $("#" + bindInfo.id);
            var jqxTreeArray = $native.find(".treemap");
            for (var i = 0; i < jqxTreeArray.length; i++) {
                var header = $(jqxTreeArray[i]).find(".treemap-header");
                var stateLED = $(jqxTreeArray[i]).find(".state-led");
                var video = $(jqxTreeArray[i]).find(".video");
                $(jqxTreeArray[i]).jqxTreeMap({ width: $native.width() });
                //$(jqxTreeArray[i]).jqxTreeMap({ height: $native.width() / 4.1 });
                $(jqxTreeArray[i]).jqxTreeMap({ height: h });
                $(jqxTreeArray[i]).append(header);
                $(jqxTreeArray[i]).append(stateLED);
                $(jqxTreeArray[i]).append(video);
            }
        });
    }

    function jqxgridSet(bindInfo, dataSetID) {
        if (!dataSetList[dataSetID] || !dataSetList[dataSetID].result) {
            console.error('Failed to bind content! dataSetID: %o', dataSetID);
            return;
        }
        var data = dataSetList[dataSetID].result;
        var optionScript = JSON.parse(bindInfo.optionScript.replace(/'/g, '"'));
        "string" == typeof data && (data = JSON.parse(data));

        //$("#" + bindInfo.id).html("");
        var w, h;
        h = $("#" + bindInfo.id).height();
        w = $("#" + bindInfo.id).width();
        if (h > 0) {
            d3ChartHeight = h;
            $("#" + bindInfo.id).height("100%")
        } else {
            h = d3ChartHeight;
        }
        if (h < 1) {
            h = 75;
        }
        var $native = $("#" + bindInfo.id);
        $native.html("");
        //var dataList = data;
        $($native).append("<div></div>");

        var source = optionScript.source;
        //var data = optionScript.data.rows;
        var option = optionScript.option;

        // prepare the data
        source.localdata = JSON.stringify(data);
        source.sortcolumn = 'g';
        source.sortdirection = 'asc';

        var dataAdapter = new $.jqx.dataAdapter(source);

        option.source = dataAdapter;
        option.width = w;
        option.height = h;
        option.groupable = true;
        //option.groupable = false;
        option.sortable = true;
        //option.editable = true;
        //option.groups = ["aa"];
        option.selectionmode = "multiplecellsadvanced";
        option.columns = [
            { "text": "설비", "datafield": "aa", width: "100px" },
            { "text": "자재군", "datafield": "equipment" },
            { "text": "자재항목", "datafield": "a" },
            { "text": "자재코드", "datafield": "b", width: 60 },
            { "text": "PO 잔량", "datafield": "c", width: 100 },
            { "text": "장착시간", "datafield": "d" },
            { "text": "잔량", "datafield": "e" },
            { "text": "단위", "datafield": "f", "columntype": "combobox" },
            { "text": "잔량(분)", "datafield": "g" },
            { "text": "KIT재고", "datafield": "h" },
            { "text": "창고재고", "datafield": "i" }
        ];
        option.columns[8].cellsrenderer = function (row, column, value, defaultHtml) {
            //"padding - bottom" 제거
            var element = $(defaultHtml);
            $(element).css("padding-bottom", "");
            if (value < 6) {
                element = $(element).css("background-color", "#eb4242");
                return element[0].outerHTML;
            } else if (value < 11) {
                element = $(element).css("background-color", "#ff8614");
                return element[0].outerHTML;
            }
            return element[0].outerHTML;
        };
        option.columns[7].columntype = "combobox";
        // /삼성
        $($($native).children()[0]).jqxGrid(option);
        $($($native).children()[0]).jqxGrid('expandallgroups');
        $($($native).children()[0]).jqxGrid({
            rendered:
                function () {
                    $(this.element).jqxGrid("expandallgroups")
                }
        });

        $(window).unbind("resize");
        $(window).bind('resize', function () {

            //var $native = $("#" + bindInfo.id);
            var jqxTreeArray = $native.children();
            for (var i = 0; i < jqxTreeArray.length; i++) {
                $(jqxTreeArray[i]).jqxGrid({ width: $native.width() })
            }
        });
    }
    function jqxmenuSet(bindInfo, dataSetID) {
        if (!dataSetList[dataSetID] || !dataSetList[dataSetID].result) {
            console.error('Failed to bind content! dataSetID: %o', dataSetID);
            return;
        }
        var data = dataSetList[dataSetID].result;
        var optionScript = JSON.parse(bindInfo.optionScript.replace(/'/g, '"'));
        "string" == typeof data && (data = JSON.parse(data));

        //$("#" + bindInfo.id).html("");
        var w, h;
        h = $("#" + bindInfo.id).height();
        w = $("#" + bindInfo.id).width();
        if (h > 0) {
            d3ChartHeight = h;
            $("#" + bindInfo.id).height("100%")
        } else {
            h = d3ChartHeight;
        }

        if (w < 1) {
            w = 600;
        }
        if (h < 1) {
            h = 30;
        }
        var $native = $("#" + bindInfo.id);
        $native.html("");
        //var dataList = data;

        //var data = [
        //        {
        //            "id": "12",
        //            "text": "Frappuccino",
        //            "parentid": "0",
        //            "subMenuWidth": '250px'
        //        },
        //        {
        //            "text": "Chocolate Beverage",
        //            "id": "1",
        //            "parentid": "0",
        //            "subMenuWidth": '250px'
        //        }, {
        //            "id": "2",
        //            "parentid": "1",
        //            "text": "Hot Chocolate"
        //        }, {
        //            "id": "3",
        //            "parentid": "1",
        //            "text": "Peppermint Hot Chocolate"
        //        }, {
        //            "id": "4",
        //            "parentid": "1",
        //            "text": "Salted Caramel Hot Chocolate"
        //        }, {
        //            "id": "5",
        //            "parentid": "1",
        //            "text": "White Hot Chocolate"
        //        }, {
        //            "id": "6",
        //            "text": "Espresso Beverage",
        //            "parentid": "0",
        //            "subMenuWidth": '200px'
        //        }, {
        //            "id": "7",
        //            "parentid": "6",
        //            "text": "Caffe Americano"
        //        }, {
        //            "id": "8",
        //            "text": "Caffe Latte",
        //            "parentid": "6"
        //        }, {
        //            "id": "9",
        //            "text": "Caffe Mocha",
        //            "parentid": "6"
        //        }, {
        //            "id": "10",
        //            "text": "Cappuccino",
        //            "parentid": "6"
        //        }, {
        //            "id": "11",
        //            "text": "Pumpkin Spice Latte",
        //            "parentid": "6"
        //        }, {
        //            "id": "13",
        //            "text": "Caffe Vanilla Frappuccino",
        //            "parentid": "12"
        //        }, {
        //            "id": "15",
        //            "text": "450 calories",
        //            "parentid": "13"
        //        }, {
        //            "id": "16",
        //            "text": "16g fat",
        //            "parentid": "13"
        //        }, {
        //            "id": "17",
        //            "text": "13g protein",
        //            "parentid": "13"
        //        }, {
        //            "id": "14",
        //            "text": "Caffe Vanilla Frappuccino Light",
        //            "parentid": "12"
        //        }]
        // prepare the data
        var source =
        {
            datatype: "json",
            datafields: [
                { name: 'id' },
                { name: 'parentid' },
                { name: 'text' },
                { name: 'subMenuWidth' }
            ],
            id: 'id',
            localdata: data
        };
        // create data adapter.
        var dataAdapter = new $.jqx.dataAdapter(source);
        // perform Data Binding.
        dataAdapter.dataBind();
        // get the menu items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents
        // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter
        // specifies the mapping between the 'text' and 'label' fields.
        var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label' }]);
        $($native).jqxMenu({ source: records, height: h, width: w, showTopLevelArrows: true });
        $($native).on('itemclick', function (event) {
            //$("#eventLog").text("Id: " + event.args.id + ", Text: " + $(event.args).text());
        });
        for (var i = 0; i < data.length; i++) {
            if (data[i].color != null) {
                $("li[item-label='" + data[i].text + "'][id='" + data[i].id + "']").css("background", data[i].color);
                //$("li[item-label='16g fat'][id='16']").css("background", "#990000");
            }
        }

        //$(window).unbind("resize");
        //$(window).bind('resize', function () {

        //    //var $native = $("#" + bindInfo.id);
        //    var jqxTreeArray = $native.children();
        //    for (var i = 0; i < jqxTreeArray.length; i++) {
        //        $(jqxTreeArray[i]).jqxGrid({ width: $native.width() })
        //    }
        //});
    }
    function d3ChartSet(bindInfo, dataSetID) {
        if (!dataSetList[dataSetID] || !dataSetList[dataSetID].result) {
            console.error('Failed to bind content! dataSetID: %o', dataSetID);
            return;
        }
        var data = dataSetList[dataSetID].result;
        "string" == typeof data && (data = JSON.parse(data));
        data = data["rows"] || data;

        $("#" + bindInfo.id).html("");
        var w, h;
        h = $("#" + bindInfo.id).height();
        w = $("#" + bindInfo.id).width();
        w = w - 15;
        h = w * 0.27;

        if (h > 0) {
            d3ChartHeight = h;
            $("#" + bindInfo.id).height("100%");
        } else {
            h = d3ChartHeight;
        }
        if (h < 1) {
            h = 75;
        }
        var x = d3.scale.linear().range([0, w]),
            y = d3.scale.linear().range([0, h]),
            color = d3.scale.category20c(),
            root, node;
        root = node = data;
        var visualizationArray = [];
        for (var i = 0; i < node.length; i++) {
            $("#" + bindInfo.id).append("<div></div>");
            var nodeData = node[i];
            var visualization = d3plus.viz()
                .container(d3.select($("#" + bindInfo.id).children()[i]))
                .data(nodeData.list)
                .type("tree_map")
                .id(["group", "name"])
                .size("size")
                .depth(1)
                .width(w)
                .height(h)
                .color("color")
                .title(nodeData.title)
                .draw();
            visualizationArray.push(visualization);
        }
        $(window).unbind("resize");
        $(window).bind('resize', function () {
            for (var i = 0; i < visualizationArray.length; i++) {
                visualizationArray[i].width($("#" + bindInfo.id).width()).draw();
            }
        });
    }
    function checkboxdataSet(bindInfo, dataSetID) {
        var $native = $("#" + bindInfo.id);
        //var options = JSON.parse(bindInfo.optionScript.replace(/'/g, '"'));
        var options = JSON.parse(bindInfo.optionScript);
        var data = micaCommon.fncS.hierarchyReturn(options.rows, options.hierarchName) || options.rows;
        micaCommon.checkBox.set($native, { local: data, textName: options.textName, valueName: options.textName });
    }
    function radiodataSet(bindInfo, dataSetID) {
        var $native = $("#" + bindInfo.id);
        //var options = JSON.parse(bindInfo.optionScript.replace(/'/g, '"'));
        var options = JSON.parse(bindInfo.optionScript);
        var data = micaCommon.fncS.hierarchyReturn(options.rows, options.hierarchName) || options.rows;
        micaCommon.radioButton.set($native, { local: data, textName: options.textName, valueName: options.textName });
    }
    function jqGridSet(bindInfo, dataSetID) {
        if (!dataSetList[dataSetID]) return;
        var data = [];
        if (dataSetList[dataSetID].result != null) {
            data = dataSetList[dataSetID].result.rows || [];
        }
        var $native = $("#" + bindInfo.id);
        //var options = JSON.parse(bindInfo.optionScript.replace(/'/g, '"'));
        var options = JSON.parse(bindInfo.optionScript);
        $native.removeClass("w-widget-jqgrid");
        var h = $native.height();
        var w = $native.width();
        if (h < 1) {
            h = 250;
        }
        $native.addClass("w-widget-jqgrid");
        if (!$native.height() > 0) {
            options.height = 219;
        } else {
            if (options.pager != null) {
                options.height = $native.height() - 31 - 24 - 1 - 2;
            } else {
                options.height = $native.height() - 31;
            }
            // 31 -> 헤더
            // 24 -> 페이지표시
            // 2 페이징 css 추가
        }
        if (options.grouping) { options.height -= 25 }
        options.onSelectRow && (options.onSelectRow = new Function(options.onSelectRow[0], options.onSelectRow[1]));
        options.onCellSelect && (options.onCellSelect = new Function(options.onCellSelect[0], options.onCellSelect[1]));
        options.beforeSelectRow && (options.beforeSelectRow = new Function(options.beforeSelectRow[0], options.beforeSelectRow[1]));
        options.gridComplete && (options.gridComplete = new Function(options.gridComplete[0], options.gridComplete[1]));
        options.onHeaderClick && (options.onHeaderClick = new Function(options.onHeaderClick[0], options.onHeaderClick[1]));
        options.afterInsertRow && (options.gridview = false, options.afterInsertRow = new Function(options.afterInsertRow[0], options.afterInsertRow[1]));
        var merge = [];
        var mergeNum = -1;
        for (var i = 0; i < options.colModel.length; i++) {
            var colModel = options.colModel[i];
            if (colModel.cellattr != null) {
                options.colModel[i].cellattr = new Function(colModel.cellattr.param, (colModel.cellattr.code).replace(/`/gi, "'"));
            }
            if (!colModel.hidden) {
                mergeNum++;
                if (colModel.merge) {
                    merge.push(mergeNum);
                }
            }
        }

        var prm = arrayParamsFix(dataSetList[dataSetID].agentParameter);
        var agentIdType = dataSetList[dataSetID].agentIdType;
        switch (agentIdType) {
            case "D":
                prm.Type = "J";
                break;
            default:
                prm.Type = "D";
                break;
        }
        prm.GUBUN = agentIdType;

        options.beforeRequest = function () {
            $("#splash").show();
        };
        options.loadComplete = function (data) {
            var table = this;
            merge = micaCommon.fncS.rangeNum(merge);
            $.each(merge, function (i, v) {
                console.log(v);
                var vSplit = v.split("~");
                if (vSplit.length > 1) {
                    micaCommon.grid.rowspan($(table), Number(vSplit[0]), Number(vSplit[1]));
                } else {
                    micaCommon.grid.rowspan($(table), Number(vSplit[0]));
                }

            });
            //micaCommon.grid.rowspan($(table), 0,2);
            setTimeout(function () {
                //table 버튼아이콘. sjjo
                //updatePagerIcons(table);
            }, 0);
            $("#splash").hide();
        };
        var params = {
            dataSetID: dataSetID
        };
        var prm = {};
        prm.dataSetID = dataSetID;

        //options.scroll = 1;
        options.datatype = "mica";
        options.postData = prm;
        //options.loadonce= true;
        options.mtype = "POST";


        //

        //options.data = [{ "CategoryName": "Beverages", "ProductName": "Ipoh Coffee", "Country": "UK", "Price": "1656.0000", "Quantity": "36" }, { "CategoryName": "Beverages", "ProductName": "Chang", "Country": "UK", "Price": "342.0000", "Quantity": "20" }, { "CategoryName": "Beverages", "ProductName": "Chartreuse verte", "Country": "USA", "Price": "648.0000", "Quantity": "42" }, { "CategoryName": "Beverages", "ProductName": "Ipoh Coffee", "Country": "USA", "Price": "1656.0000", "Quantity": "39" }, { "CategoryName": "Beverages", "ProductName": "Chai", "Country": "UK", "Price": "1314.0000", "Quantity": "73" }, { "CategoryName": "Condiments", "ProductName": "Original Frankfurter grüne Soße", "Country": "USA", "Price": "988.0000", "Quantity": "82" }, { "CategoryName": "Condiments", "ProductName": "Louisiana Fiery Hot Pepper Sauce", "Country": "USA", "Price": "904.3500", "Quantity": "47" }, { "CategoryName": "Condiments", "ProductName": "Chef Anton's Gumbo Mix", "Country": "UK", "Price": "1281.0000", "Quantity": "60" }, { "CategoryName": "Condiments", "ProductName": "Louisiana Hot Spiced Okra", "Country": "UK", "Price": "408.0000", "Quantity": "24" }, { "CategoryName": "Condiments", "ProductName": "Chef Anton's Cajun Seasoning", "Country": "UK", "Price": "550.0000", "Quantity": "25" }, { "CategoryName": "Condiments", "ProductName": "Chef Anton's Gumbo Mix", "Country": "USA", "Price": "1783.9000", "Quantity": "86" }, { "CategoryName": "Confections", "ProductName": "Maxilaku", "Country": "USA", "Price": "1260.0000", "Quantity": "63" }, { "CategoryName": "Confections", "ProductName": "Teatime Chocolate Biscuits", "Country": "USA", "Price": "1278.2000", "Quantity": "141" }, { "CategoryName": "Confections", "ProductName": "Sir Rodney's Scones", "Country": "USA", "Price": "1390.0000", "Quantity": "139" }, { "CategoryName": "Confections", "ProductName": "Tarte au sucre", "Country": "USA", "Price": "16953.8000", "Quantity": "371" }, { "CategoryName": "Dairy Products", "ProductName": "Mozzarella di Giovanni", "Country": "UK", "Price": "1356.8000", "Quantity": "41" }, { "CategoryName": "Dairy Products", "ProductName": "Geitost", "Country": "UK", "Price": "136.5000", "Quantity": "57" }, { "CategoryName": "Dairy Products", "ProductName": "Raclette Courdavault", "Country": "UK", "Price": "770.0000", "Quantity": "14" }, { "CategoryName": "Dairy Products", "ProductName": "Queso Manchego La Pastora", "Country": "USA", "Price": "4636.0000", "Quantity": "122" }, { "CategoryName": "Dairy Products", "ProductName": "Gudbrandsdalsost", "Country": "UK", "Price": "972.0000", "Quantity": "33" }, { "CategoryName": "Dairy Products", "ProductName": "Fløtemysost", "Country": "UK", "Price": "1689.9000", "Quantity": "82" }, { "CategoryName": "Dairy Products", "ProductName": "Gorgonzola Telino", "Country": "USA", "Price": "2832.5000", "Quantity": "241" }, { "CategoryName": "Dairy Products", "ProductName": "Queso Cabrales", "Country": "UK", "Price": "1365.0000", "Quantity": "65" }, { "CategoryName": "Dairy Products", "ProductName": "Mascarpone Fabioli", "Country": "USA", "Price": "1056.0000", "Quantity": "41" }, { "CategoryName": "Dairy Products", "ProductName": "Camembert Pierrot", "Country": "UK", "Price": "4590.0000", "Quantity": "151" }, { "CategoryName": "Dairy Products", "ProductName": "Queso Cabrales", "Country": "USA", "Price": "1113.0000", "Quantity": "53" }, { "CategoryName": "Dairy Products", "ProductName": "Geitost", "Country": "USA", "Price": "500.5000", "Quantity": "221" }, { "CategoryName": "Dairy Products", "ProductName": "Mozzarella di Giovanni", "Country": "USA", "Price": "3763.6000", "Quantity": "117" }, { "CategoryName": "Dairy Products", "ProductName": "Fløtemysost", "Country": "USA", "Price": "4364.5000", "Quantity": "215" }, { "CategoryName": "Dairy Products", "ProductName": "Raclette Courdavault", "Country": "USA", "Price": "14080.0000", "Quantity": "276" }, { "CategoryName": "Dairy Products", "ProductName": "Gudbrandsdalsost", "Country": "USA", "Price": "3600.0000", "Quantity": "100" }, { "CategoryName": "Dairy Products", "ProductName": "Gorgonzola Telino", "Country": "UK", "Price": "700.0000", "Quantity": "70" }, { "CategoryName": "Dairy Products", "ProductName": "Camembert Pierrot", "Country": "USA", "Price": "5603.2000", "Quantity": "173" }, { "CategoryName": "Dairy Products", "ProductName": "Mascarpone Fabioli", "Country": "UK", "Price": "768.0000", "Quantity": "24" }, { "CategoryName": "Grains/Cereals", "ProductName": "Ravioli Angelo", "Country": "UK", "Price": "117.0000", "Quantity": "6" }, { "CategoryName": "Grains/Cereals", "ProductName": "Gustaf's Knäckebröd", "Country": "UK", "Price": "642.6000", "Quantity": "33" }, { "CategoryName": "Grains/Cereals", "ProductName": "Filo Mix", "Country": "UK", "Price": "154.0000", "Quantity": "26" }, { "CategoryName": "Grains/Cereals", "ProductName": "Tunnbröd", "Country": "UK", "Price": "736.2000", "Quantity": "86" }, { "CategoryName": "Grains/Cereals", "ProductName": "Ravioli Angelo", "Country": "USA", "Price": "1072.5000", "Quantity": "65" }, { "CategoryName": "Grains/Cereals", "ProductName": "Filo Mix", "Country": "USA", "Price": "252.0000", "Quantity": "36" }, { "CategoryName": "Grains/Cereals", "ProductName": "Singaporean Hokkien Fried Mee", "Country": "USA", "Price": "1246.0000", "Quantity": "89" }, { "CategoryName": "Grains/Cereals", "ProductName": "Gnocchi di nonna Alice", "Country": "USA", "Price": "12980.8000", "Quantity": "366" }, { "CategoryName": "Grains/Cereals", "ProductName": "Tunnbröd", "Country": "USA", "Price": "414.0000", "Quantity": "46" }, { "CategoryName": "Grains/Cereals", "ProductName": "Gustaf's Knäckebröd", "Country": "USA", "Price": "1008.0000", "Quantity": "48" }, { "CategoryName": "Grains/Cereals", "ProductName": "Wimmers gute Semmelknödel", "Country": "USA", "Price": "3458.0000", "Quantity": "110" }, { "CategoryName": "Grains/Cereals", "ProductName": "Wimmers gute Semmelknödel", "Country": "UK", "Price": "239.4000", "Quantity": "9" }, { "CategoryName": "Grains/Cereals", "ProductName": "Gnocchi di nonna Alice", "Country": "UK", "Price": "1178.0000", "Quantity": "35" }, { "CategoryName": "Meat/Poultry", "ProductName": "Perth Pasties", "Country": "USA", "Price": "5916.0000", "Quantity": "186" }, { "CategoryName": "Meat/Poultry", "ProductName": "Mishi Kobe Niku", "Country": "UK", "Price": "291.0000", "Quantity": "3" }, { "CategoryName": "Meat/Poultry", "ProductName": "Thüringer Rostbratwurst", "Country": "UK", "Price": "2079.0000", "Quantity": "21" }, { "CategoryName": "Meat/Poultry", "ProductName": "Pâté chinois", "Country": "USA", "Price": "4008.0000", "Quantity": "191" }, { "CategoryName": "Meat/Poultry", "ProductName": "Pâté chinois", "Country": "UK", "Price": "840.0000", "Quantity": "35" }, { "CategoryName": "Meat/Poultry", "ProductName": "Thüringer Rostbratwurst", "Country": "USA", "Price": "19705.1600", "Quantity": "173" }, { "CategoryName": "Meat/Poultry", "ProductName": "Tourtière", "Country": "USA", "Price": "1673.3000", "Quantity": "240" }, { "CategoryName": "Meat/Poultry", "ProductName": "Alice Mutton", "Country": "USA", "Price": "13509.6000", "Quantity": "361" }, { "CategoryName": "Meat/Poultry", "ProductName": "Alice Mutton", "Country": "UK", "Price": "975.0000", "Quantity": "25" }, { "CategoryName": "Meat/Poultry", "ProductName": "Tourtière", "Country": "UK", "Price": "52.1500", "Quantity": "7" }, { "CategoryName": "Meat/Poultry", "ProductName": "Perth Pasties", "Country": "UK", "Price": "1113.6000", "Quantity": "42" }, { "CategoryName": "Meat/Poultry", "ProductName": "Mishi Kobe Niku", "Country": "USA", "Price": "582.0000", "Quantity": "6" }, { "CategoryName": "Produce", "ProductName": "Rössle Sauerkraut", "Country": "USA", "Price": "1549.2000", "Quantity": "37" }, { "CategoryName": "Produce", "ProductName": "Manjimup Dried Apples", "Country": "USA", "Price": "3286.0000", "Quantity": "62" }, { "CategoryName": "Produce", "ProductName": "Uncle Bob's Organic Dried Pears", "Country": "USA", "Price": "3780.0000", "Quantity": "131" }, { "CategoryName": "Produce", "ProductName": "Tofu", "Country": "USA", "Price": "1850.7000", "Quantity": "91" }, { "CategoryName": "Produce", "ProductName": "Rössle Sauerkraut", "Country": "UK", "Price": "1822.4000", "Quantity": "44" }, { "CategoryName": "Produce", "ProductName": "Uncle Bob's Organic Dried Pears", "Country": "UK", "Price": "3840.0000", "Quantity": "134" }, { "CategoryName": "Produce", "ProductName": "Manjimup Dried Apples", "Country": "UK", "Price": "1420.4000", "Quantity": "31" }, { "CategoryName": "Seafood", "ProductName": "Konbu", "Country": "UK", "Price": "202.8000", "Quantity": "34" }, { "CategoryName": "Seafood", "ProductName": "Spegesild", "Country": "UK", "Price": "180.0000", "Quantity": "15" }, { "CategoryName": "Seafood", "ProductName": "Jack's New England Clam Chowder", "Country": "USA", "Price": "2139.8500", "Quantity": "227" }, { "CategoryName": "Seafood", "ProductName": "Escargots de Bourgogne", "Country": "USA", "Price": "1073.2500", "Quantity": "87" }, { "CategoryName": "Seafood", "ProductName": "Nord-Ost Matjeshering", "Country": "UK", "Price": "388.3500", "Quantity": "15" }, { "CategoryName": "Seafood", "ProductName": "Ikura", "Country": "USA", "Price": "2945.0000", "Quantity": "95" }, { "CategoryName": "Seafood", "ProductName": "Spegesild", "Country": "USA", "Price": "1368.0000", "Quantity": "120" }, { "CategoryName": "Seafood", "ProductName": "Røgede sild", "Country": "USA", "Price": "1767.0000", "Quantity": "186" }, { "CategoryName": "Seafood", "ProductName": "Gravad lax", "Country": "USA", "Price": "1560.0000", "Quantity": "60" }, { "CategoryName": "Seafood", "ProductName": "Inlagd Sill", "Country": "USA", "Price": "3515.0000", "Quantity": "185" }, { "CategoryName": "Seafood", "ProductName": "Røgede sild", "Country": "UK", "Price": "114.0000", "Quantity": "15" }, { "CategoryName": "Seafood", "ProductName": "Konbu", "Country": "USA", "Price": "1560.0000", "Quantity": "262" }, { "CategoryName": "Seafood", "ProductName": "Röd Kaviar", "Country": "USA", "Price": "1230.0000", "Quantity": "82" }, { "CategoryName": "Seafood", "ProductName": "Gravad lax", "Country": "UK", "Price": "468.0000", "Quantity": "18" }, { "CategoryName": "Seafood", "ProductName": "Jack's New England Clam Chowder", "Country": "UK", "Price": "211.9000", "Quantity": "26" }, { "CategoryName": "Seafood", "ProductName": "Inlagd Sill", "Country": "UK", "Price": "1140.0000", "Quantity": "60" }, { "CategoryName": "Seafood", "ProductName": "Ikura", "Country": "UK", "Price": "1116.0000", "Quantity": "36" }, { "CategoryName": "Seafood", "ProductName": "Boston Crab Meat", "Country": "USA", "Price": "1876.6000", "Quantity": "104" }, { "CategoryName": "Seafood", "ProductName": "Boston Crab Meat", "Country": "UK", "Price": "147.0000", "Quantity": "10" }, { "CategoryName": "Seafood", "ProductName": "Carnarvon Tigers", "Country": "USA", "Price": "5187.5000", "Quantity": "88" }, { "CategoryName": "Seafood", "ProductName": "Nord-Ost Matjeshering", "Country": "USA", "Price": "926.3700", "Quantity": "44" }];
        //options.datatype = "local";
        //


        $native.empty();
        $native.html("<table id='table_" + bindInfo.id + "'></table>");
        if (options.pager != null) {
            $native.append("<div class='m-pager' id='page_" + bindInfo.id + "'></div>");
            options.pager = $("#page_" + bindInfo.id);
        }
        //KHP 임시 조치
        var navGrid = options.navGrid;
        delete (options.navGrid);

        //Muti-language 필요시
        //options.colModel = Mica.require('lang').apply(options.colModel) || options.colModel;
        for (var i = 0; i < options.colModel.length; i++) {
            if (options.colModel[i].edittype == "notSet") {
                delete options.colModel[i].edittype;
            }
        }
        delete options.colModel[0].edittype;
        if (options.pivotOptions != null && options.usePivot) {
            //delete options.simple.simple;
            //var simple = { "label": "Quantity", "name": "Quantity", "formatoptions": {}, "width": "auto", "sortable": true, "align": "left", "colModel": [{ "name": "CategoryName", "width": 80, "editable": true }], "height": "auto", "rowNum": 10, "rowList": ["\"10:10\", \"20:20\", \"30:30\", \"1000:ALL\""] };
            if (options.simple) {
                options.simple.rowNum = options.rowNum;
                options.simple.pager = options.pager;
                options.simple.rowList = options.rowList;

                if (options.pivotOptions.colTotals || options.pivotOptions.rowTotals) {
                    options.simple.height = h - 40 + 1;
                    //khp 09-08                    options.simple.height = h + 1 - 20;
                } else {
                    options.simple.height = h + 1;
                }
                $("#table_" + bindInfo.id).jqGrid("jqPivot", data, options.pivotOptions, options.simple);
            } else {
                options.simple = {};
                options.simple.rowNum = options.rowNum;
                options.simple.pager = options.pager;
                options.simple.rowList = options.rowList;

                $("#table_" + bindInfo.id).jqGrid("jqPivot", data, options.pivotOptions);
            }
        } else {
            $("#table_" + bindInfo.id).jqGrid(options);
        }

        if (options.setGroupHeadersNotFnc != null) {
            if (options.setGroupHeadersNotFnc.true) {
                $("#table_" + bindInfo.id).jqGrid('setGroupHeaders', options.setGroupHeadersNotFnc.value);
            }
        }

        //$("#jqgrid").html("<table id='grid'></table><div id='pager'></div>");
        //var data = [{ "OrderID": "10248", "OrderDate": "1996-07-04 00:00:00", "ShipName": "Vins et alcools Chevalier" }, { "OrderID": "10249", "OrderDate": "1996-07-05 00:00:00", "ShipName": "Toms Spezialitäten" }, { "OrderID": "10250", "OrderDate": "1996-07-08 00:00:00", "ShipName": "Hanari Carnes" }, { "OrderID": "10251", "OrderDate": "1996-07-08 00:00:00", "ShipName": "Victuailles en stock" }, { "OrderID": "10252", "OrderDate": "1970-01-01 00:00:00", "ShipName": "Suprêmes délices" }, { "OrderID": "10253", "OrderDate": "1996-07-10 00:00:00", "ShipName": "Hanari Carnes" }, { "OrderID": "10254", "OrderDate": "1996-07-11 00:00:00", "ShipName": "Chop-suey Chinese" }, { "OrderID": "10255", "OrderDate": "1996-07-12 00:00:00", "ShipName": "Richter Supermarkt" }, { "OrderID": "10256", "OrderDate": "1996-07-15 00:00:00", "ShipName": "Wellington Importadora" }, { "OrderID": "10257", "OrderDate": "1996-07-16 00:00:00", "ShipName": "HILARIÓN-Abastos" }, { "OrderID": "10258", "OrderDate": "1996-07-17 00:00:00", "ShipName": "Ernst Handel" }, { "OrderID": "10259", "OrderDate": "1996-07-18 00:00:00", "ShipName": "Centro comercial Moctezuma" }, { "OrderID": "10260", "OrderDate": "1996-07-19 00:00:00", "ShipName": "Ottilies Käseladen" }, { "OrderID": "10261", "OrderDate": "1996-07-19 00:00:00", "ShipName": "Que Delícia" }, { "OrderID": "10262", "OrderDate": "1996-07-22 00:00:00", "ShipName": "Rattlesnake Canyon Grocery" }];
        //jQuery('#grid').jqGrid({
        //    hoverrows: false,
        //    "viewrecords": true,
        //    "jsonReader": { "repeatitems": false, "subgrid": { "repeatitems": false } },
        //    "gridview": true,
        //    //"url": "/content/jqgridmobile/mobilejqgrid.json",
        //    "loadonce": true,
        //    "rowNum": 100,
        //    "height": "400px",
        //    "autowidth": true,
        //    "sortname": "OrderID",
        //    "rowList": [10, 30, 40],
        //    //"datatype": "json",
        //    data: data,
        //    datatype: "local",
        //    "colModel": [
        //        { "name": "OrderID", "index": "OrderID", "sorttype": "int", "key": true, "editable": true },
        //        {
        //            "name": "OrderDate", "index": "OrderDate", "sorttype": "datetime", "formatter": "date",
        //            "formatoptions": { "srcformat": "Y-m-d H:i:s", "newformat": "m/d/Y" },
        //            "search": false, "editable": true
        //        },
        //        { "name": "ShipName", "index": "ShipName", "sorttype": "string", "classes": "ui-ellipsis", "editable": true }
        //    ],
        //    //"colModel": [
        //    //    { "name": "OrderID", "index": "OrderID" },
        //    //    {
        //    //        "name": "OrderDate", "index": "OrderDate"
        //    //    },
        //    //    { "name": "ShipName", "index": "ShipName" }
        //    //],
        //    "pager": "#pager"
        //});
        //2015-03-25 그리드사이즈
        $(window).bind('resize', function () {
            $("#table_" + bindInfo.id).setGridWidth($("#" + bindInfo.id).width());
            var groupHeaders = JSON.parse(JSON.stringify($("#table_" + bindInfo.id).jqGrid("getGridParam", "groupHeader")));
            if (groupHeaders != null) {
                $("#table_" + bindInfo.id).jqGrid("destroyGroupHeader").jqGrid("setGroupHeaders", groupHeaders[0]);
            }
        }).trigger('resize');

        if (options.pager != null) {
            if (navGrid) {
                $('#table_' + bindInfo.id).jqGrid('navGrid', '#page_' + bindInfo.id,
                    { 	//navbar options
                        edit: navGrid.edit ? true : false, edittext: "edit",
                        editicon: 'fa fa-pencil text-blue',
                        add: navGrid.add ? true : false, addtext: "add",
                        addicon: 'fa fa-plus-circle text-purple',
                        del: navGrid.del ? true : false, deltext: "del",
                        delicon: 'fa fa-trash-o text-red',
                        search: navGrid.search ? true : false, searchtext: "search",
                        searchicon: 'fa fa-search text-orange',
                        refresh: navGrid.refresh ? true : false, refreshtext: "refresh",
                        refreshicon: 'fa-refresh text-green',
                        view: navGrid.view ? true : false, viewtext: "view",
                        viewicon: 'fa fa-search-plus text-grey'
                    }
                    , {
                        //edit record form
                        //closeAfterEdit: true,
                        //width: 700,
                        recreateForm: true,
                        onclickSubmit: function (options, postData) {
                            return {
                                sortColumnName: "ProductName",
                                sortOrder: "asc",
                                rowNum: 15
                            };
                        },
                        beforeShowForm: function (e) {
                            var form = $(e[0]);
                            form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
                            style_edit_form(form);
                        }
                    },
                    {
                        //new record form
                        //width: 700,
                        closeAfterAdd: true,
                        recreateForm: true,
                        viewPagerButtons: false,
                        beforeShowForm: function (e) {
                            var form = $(e[0]);
                            form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar')
                    		.wrapInner('<div class="widget-header" />');
                            style_edit_form(form);
                        }
                    },
                    {
                        //delete record form
                        recreateForm: true,
                        beforeShowForm: function (e) {
                            var form = $(e[0]);
                            if (form.data('styled')) return false;

                            form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
                            style_delete_form(form);

                            form.data('styled', true);
                        },
                        onClick: function (e) {
                            //alert(1);
                        }
                    },
                    {
                        //search form
                        recreateForm: true,
                        afterShowSearch: function (e) {
                            var form = $(e[0]);
                            form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />');
                            style_search_form(form);
                        },
                        afterRedraw: function () {
                            style_search_filters($(this));
                        }
                    	,
                        multipleSearch: false,
                    },
                    {
                        //view record form
                        recreateForm: true,
                        beforeShowForm: function (e) {
                            var form = $(e[0]);
                            form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />')
                        }
                    }
                );
                if (navGrid.excel) {
                    $('#table_' + bindInfo.id).jqGrid('navButtonAdd', '#page_' + bindInfo.id, {
                        caption: "Excel",
                        buttonicon: "fa fa-file-excel-o",
                        onClickButton: function () {
                            micaCommon.grid.download.xlsx($(this));
                        },
                        position: "last"
                    });
                };

                //$('#table_' + bindInfo.id).
            }
        }

        setTimeout(function (e) {
            $("[splitter].jqx-widget").trigger("resize");
        }, 0);

        function style_edit_form(form) {
            //enable datepicker on "sdate" field and switches for "stock" field
            form.find('input[name=sdate]').datepicker({ format: 'yyyy-mm-dd', autoclose: true });

            form.find('input[name=stock]').addClass('ace ace-switch ace-switch-5').after('<span class="lbl"></span>');
            //don't wrap inside a label element, the checkbox value won't be submitted (POST'ed)
            //.addClass('ace ace-switch ace-switch-5').wrap('<label class="inline" />').after('<span class="lbl"></span>');


            //update buttons classes
            var buttons = form.next().find('.EditButton .fm-button');
            buttons.addClass('btn btn-sm').find('[class*="-icon"]').hide();//ui-icon, s-icon
            buttons.eq(0).addClass('btn-primary').prepend('<i class="fa fa-check"></i>');
            buttons.eq(1).addClass('btn-default').prepend('<i class="fa fa-times"></i>');

            buttons = form.next().find('.navButton a');
            buttons.find('.ui-icon').hide();
            buttons.eq(0).append('<i class="fa fa-chevron-left"></i>');
            buttons.eq(1).append('<i class="fa fa-chevron-right"></i>');
        }

        function style_delete_form(form) {
            var buttons = form.next().find('.EditButton .fm-button');
            buttons.addClass('btn btn-sm btn-white btn-round').find('[class*="-icon"]').hide();//ui-icon, s-icon
            buttons.eq(0).addClass('btn-danger').prepend('<i class="fa fa-trash-o"></i>');
            buttons.eq(1).addClass('btn-default').prepend('<i class="fa fa-times"></i>');
        }

        function style_search_filters(form) {
            form.find('.delete-rule').val('X');
            form.find('.add-rule').addClass('btn btn-xs btn-primary');
            form.find('.add-group').addClass('btn btn-xs btn-success');
            form.find('.delete-group').addClass('btn btn-xs btn-danger');
        }
        function style_search_form(form) {
            var dialog = form.closest('.ui-jqdialog');
            var buttons = dialog.find('.EditTable');
            buttons.find('.EditButton a[id*="_reset"]').addClass('btn btn-sm btn-default').find('.ui-icon').attr('class', 'fa fa-retweet');
            buttons.find('.EditButton a[id*="_query"]').addClass('btn btn-sm btn-inverse').find('.ui-icon').attr('class', 'fa fa-comment-o');
            buttons.find('.EditButton a[id*="_search"]').addClass('btn btn-sm btn-primary').find('.ui-icon').attr('class', 'fa fa-search');
        }

        function beforeDeleteCallback(e) {
            var form = $(e[0]);
            if (form.data('styled')) return false;

            form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
            style_delete_form(form);

            form.data('styled', true);
        }

        function beforeEditCallback(e) {
            var form = $(e[0]);
            form.closest('.ui-jqdialog').find('.ui-jqdialog-titlebar').wrapInner('<div class="widget-header" />');
            style_edit_form(form);
        }



        /*
                        if (options.pager != null) {
                            if (true) {
                                $('#table_' + bindInfo.id).navGrid('#page_' + bindInfo.id,
                                    // the buttons to appear on the toolbar of the grid
                                    { edit: true, add: true, del: true, search: false, refresh: false, view: false, position: "left", cloneToTop: false },
                                    // options for the Edit Dialog
                                    {
                                        editCaption: "The Edit Dialog",
                                        template: template,
                                        errorTextFormat: function (data) {
                                            return 'Error: ' + data.responseText
                                        }
                                    },
                                    // options for the Add Dialog
                                    {
                                        template: template,
                                        errorTextFormat: function (data) {
                                            return 'Error: ' + data.responseText
                                        }
                                    },
                                    // options for the Delete Dailog
                                    {
                                        errorTextFormat: function (data) {
                                            return 'Error: ' + data.responseText
                                        }
                                    }
                                );
                            }

                        }
                        */
        //        $(window).trigger('resize');
    }
    api.paging = function (params) {
        var dataSetResult = dataSetList[params.dataSetID].result;
        var objectName = dataSetList[params.dataSetID].objectName;
        var rows = [];

        if (dataSetResult == null) {
            var returnval = {};

            returnval.total = 0;
            returnval.result = "success";
            returnval.rows = [];
            returnval.page = 1;
            returnval.records = 0;
            return returnval;
        }
        var rows = dataSetResult.rows;
        if (rows == null) {
            if (objectName == null) {
                rows = dataSetResult;
            } else {
                rows = dataSetResult[objectName];
            }
        }
        //var total = dataSetResult.total; //totalpage
        var records = dataSetResult.records;
        var page = params.page;
        var rowsNum = params.rows;

        //khp
        records === undefined && (records = rows.length);
        page === undefined && (page = 1);

        var total = 1;
        if (rowsNum < records) {
            total = Math.ceil(records / rowsNum);
        }

        var startNum = rowsNum * page - rowsNum;
        var endNum = rowsNum * page;
        if (endNum > records) {
            endNum = records;
        }
        if (params.sidx != "") {
            sortFnc.params = params;
            rows.sort(sortFnc);
        }

        //dataSetList[params.dataSetID].agentParameter[4].value = 10;
        var pagerows = [];
        for (var i = startNum; i < endNum; i++) {
            if (rows[i] == null) {
                var url = dataSetList[params.dataSetID].url;
                var agentParameterArray = dataSetList[params.dataSetID].agentParameter;
                var dataSetRows = 0;
                var dataSetPage = 0;
                var paramsObj = {};
                for (var name in agentParameterArray) {
                    if (agentParameterArray[name].name == "rows") {
                        dataSetRows = name;
                    } else if (agentParameterArray[name].name == "page") {
                        agentParameterArray[name].value++;
                        dataSetPage = name;
                    }
                    switch (agentParameterArray[name].type) {
                        case "B":
                            paramsObj[agentParameterArray[name].name] = agentParameterArray[name].value;
                            break;
                        case "N":
                            paramsObj[agentParameterArray[name].name] = agentParameterArray[name].value;
                            break;
                        default:
                            paramsObj[agentParameterArray[name].name] = agentParameterArray[name].value;
                            break;
                    }
                }
                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(paramsObj),
                    success: function (data) {
                        var objectName = dataSetList[params.dataSetID].objectName;
                        if (objectName != null) {
                            dataSetList[params.dataSetID].result[objectName] = dataSetList[params.dataSetID].result[objectName].concat(data[objectName]);
                            rows = dataSetList[params.dataSetID].result[objectName];
                            //dataSetList[params.dataSetID].result[objectName].push(data[objectName][0]);
                        }
                    },
                    async: false
                });
                //$.post(url, JSON.stringify(paramsObj)).done(function (data) {
                //    //$('#splash').hide();
                //    var objectName = dataSetList[params.dataSetID].objectName;
                //    if (objectName != null) {
                //        dataSetList[params.dataSetID].result[objectName].push(data[objectN ame]);
                //    }
                //});
                //break;
            }
            pagerows.push(rows[i]);
        }
        //            return dataSetResult;
        var returnval = {};

        returnval.total = total;
        returnval.result = "success";
        returnval.rows = pagerows;
        returnval.page = page;
        returnval.records = records;
        return returnval;
        //return dataSetResult;
    };
    var sortFnc = function (first, second) {
        //var sidx = sortFnc.params.sidx;
        var sidx = sortFnc.params.sidx.split(" ");
        sidx = sidx[0];
        var sord = sortFnc.params.sord;
        var num = 0;
        if (first[sidx] < second[sidx]) {
            num = -1;
        } else if (first[sidx] > second[sidx]) {
            num = 1;
        }

        if (sord == "desc") {
            return num * -1;
        }
        return num;
        //params.sidx, params.sord;
    };

    // Export module
    return api;
});


/**
 * ----------------------------------------------------------------------
 * Mica: Maps widget
 */
Mica.define('maps', function ($, _) {
    //'use strict';

    var api = {};
    var $doc = $(document);
    var google = null;
    var $maps;
    var namespace = '.w-widget-map';

    // -----------------------------------
    // Module methods

    api.ready = function () {
        // Init Maps on the front-end
        if (!Mica.env()) initMaps();
    };

    api.preview = function () {
        // Update active map nodes
        $maps = $doc.find(namespace);
        // Listen for resize events
        Mica.resize.off(triggerRedraw);
        if ($maps.length) {
            Mica.resize.on(triggerRedraw);
            triggerRedraw();
        }
    };

    api.design = function (evt) {
        // Update active map nodes
        $maps = $doc.find(namespace);
        // Stop listening for resize events
        Mica.resize.off(triggerRedraw);
        // Redraw to account for page changes
        $maps.length && _.defer(triggerRedraw);
    };

    api.destroy = removeListeners;

    // -----------------------------------
    // Private methods

    // Trigger redraw in designer or preview mode
    function triggerRedraw() {
        if ($maps.length && Mica.app) {
            $maps.each(Mica.app.redrawElement);
        }
    }

    function initMaps() {
        $maps = $doc.find(namespace);
        if (!$maps.length) return;

        if (google === null) {
            $.getScript('https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&callback=_wf_maps_loaded');
            window._wf_maps_loaded = mapsLoaded;
        } else {
            mapsLoaded();
        }

        function mapsLoaded() {
            window._wf_maps_loaded = function () { };
            google = window.google;
            $maps.each(renderMap);
            removeListeners();
            addListeners();
        }
    }

    function removeListeners() {
        Mica.resize.off(resizeMaps);
        Mica.redraw.off(resizeMaps);
    }

    function addListeners() {
        Mica.resize.on(resizeMaps);
        Mica.redraw.on(resizeMaps);
    }

    // Render map onto each element
    function renderMap(i, el) {
        var data = $(el).data();
        getState(el, data);
    }

    function resizeMaps() {
        $maps.each(resizeMap);
    }

    // Resize map when window changes
    function resizeMap(i, el) {
        var state = getState(el);
        google.maps.event.trigger(state.map, 'resize');
        state.setMapPosition();
    }

    // Store state on element data
    var store = 'w-widget-map';
    function getState(el, data) {

        var state = $.data(el, store);
        if (state) return state;

        var $el = $(el);
        state = $.data(el, store, {
            // Default options
            latLng: '51.511214,-0.119824',
            tooltip: '',
            style: 'roadmap',
            zoom: 12,

            // Marker
            marker: new google.maps.Marker({
                draggable: false
            }),

            // Tooltip infowindow
            infowindow: new google.maps.InfoWindow({
                disableAutoPan: true
            })
        });

        // LatLng center point
        var latLng = data.widgetLatlng || state.latLng;
        state.latLng = latLng;
        var coords = latLng.split(',');
        var latLngObj = new google.maps.LatLng(coords[0], coords[1]);
        state.latLngObj = latLngObj;

        // Disable touch events
        var mapDraggable = (Mica.env.touch && data.disableTouch) ? false : true;

        // Map instance
        state.map = new google.maps.Map(el, {
            center: state.latLngObj,
            zoom: state.zoom,
            maxZoom: 18,
            mapTypeControl: false,
            panControl: false,
            streetViewControl: false,
            scrollwheel: !data.disableScroll,
            draggable: mapDraggable,
            zoomControl: true,
            zoomControlOptions: {
                style: google.maps.ZoomControlStyle.SMALL
            },
            mapTypeId: state.style
        });
        state.marker.setMap(state.map);

        // Set map position and offset
        state.setMapPosition = function () {
            state.map.setCenter(state.latLngObj);
            var offsetX = 0;
            var offsetY = 0;
            var padding = $el.css(['paddingTop', 'paddingRight', 'paddingBottom', 'paddingLeft']);
            offsetX -= parseInt(padding.paddingLeft, 10);
            offsetX += parseInt(padding.paddingRight, 10);
            offsetY -= parseInt(padding.paddingTop, 10);
            offsetY += parseInt(padding.paddingBottom, 10);
            if (offsetX || offsetY) {
                state.map.panBy(offsetX, offsetY);
            }
            $el.css('position', ''); // Remove injected position
        };

        // Fix position after first tiles have loaded
        google.maps.event.addListener(state.map, 'tilesloaded', function () {
            google.maps.event.clearListeners(state.map, 'tilesloaded');
            state.setMapPosition();
        });

        // Set initial position
        state.setMapPosition();
        state.marker.setPosition(state.latLngObj);
        state.infowindow.setPosition(state.latLngObj);

        // Draw tooltip
        var tooltip = data.widgetTooltip;
        if (tooltip) {
            state.tooltip = tooltip;
            state.infowindow.setContent(tooltip);
            if (!state.infowindowOpen) {
                state.infowindow.open(state.map, state.marker);
                state.infowindowOpen = true;
            }
        }

        // Map style - options.style
        var style = data.widgetStyle;
        if (style) {
            state.map.setMapTypeId(style);
        }

        // Zoom - options.zoom
        var zoom = data.widgetZoom;
        if (zoom != null) {
            state.zoom = zoom;
            state.map.setZoom(+zoom);
        }

        // Click marker to open in google maps
        google.maps.event.addListener(state.marker, 'click', function () {
            window.open('https://maps.google.com/?z=' + state.zoom + '&daddr=' + state.latLng);
        });

        return state;
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Google+ widget
 */
Mica.define('gplus', function ($) {
    //'use strict';

    var $doc = $(document);
    var api = {};
    var loaded;

    api.ready = function () {
        // Load Google+ API on the front-end
        if (!Mica.env() && !loaded) init();
    };

    function init() {
        $doc.find('.w-widget-gplus').length && load();
    }

    function load() {
        loaded = true;
        $.getScript('https://apis.google.com/js/plusone.js');
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Smooth scroll
 */
Mica.define('scroll', function ($) {
    //'use strict';

    var $doc = $(document);
    var win = window;
    var loc = win.location;
    var history = win.history;
    var validHash = /^[a-zA-Z][\w:.-]*$/;

    function ready() {
        // If hash is already present on page load, scroll to it right away
        if (loc.hash) {
            findEl(loc.hash.substring(1));
        }

        // When clicking on a link, check if it links to another part of the page
        $doc.on('click', 'a', function (e) {
            if (Mica.env('design')) {
                return;
            }

            // Ignore links being used by jQuery mobile
            if (window.$.mobile && $(e.currentTarget).hasClass('ui-link')) return;

            // Ignore empty # links
            if (this.getAttribute('href') === '#') {
                e.preventDefault();
                return;
            }

            var hash = this.hash ? this.hash.substring(1) : null;
            if (hash) {
                findEl(hash, e);
            }
        });
    }

    function findEl(hash, e) {
        if (!validHash.test(hash)) return;

        var el = $('#' + hash);
        if (!el.length) {
            return;
        }

        if (e) {
            e.preventDefault();
            e.stopPropagation();
        }

        // Push new history state
        if (loc.hash !== hash && history && history.pushState) {
            var oldHash = history.state && history.state.hash;
            if (oldHash !== hash) {
                history.pushState({ hash: hash }, '', '#' + hash);
            }
        }

        // If a fixed header exists, offset for the height
        var header = $('header, body > .header, body > .w-nav');
        var offset = header.css('position') === 'fixed' ? header.outerHeight() : 0;

        win.setTimeout(function () {
            scroll(el, offset);
        }, e ? 0 : 300);
    }

    function scroll(el, offset) {
        var start = $(win).scrollTop();
        var end = el.offset().top - offset;

        // If specified, scroll so that the element ends up in the middle of the viewport
        if (el.data('scroll') == 'mid') {
            var available = $(win).height() - offset;
            var elHeight = el.outerHeight();
            if (elHeight < available) {
                end -= Math.round((available - elHeight) / 2);
            }
        }

        var mult = 1;

        // Check for custom time multiplier on the body and the element
        $('body').add(el).each(function (i) {
            var time = parseFloat($(this).attr('data-scroll-time'), 10);
            if (!isNaN(time) && (time === 0 || time > 0)) {
                mult = time;
            }
        });

        // Shim for IE8 and below
        if (!Date.now) {
            Date.now = function () { return new Date().getTime(); };
        }

        var clock = Date.now();
        var animate = win.requestAnimationFrame || win.mozRequestAnimationFrame || win.webkitRequestAnimationFrame || function (fn) { win.setTimeout(fn, 15); };
        var duration = (472.143 * Math.log(Math.abs(start - end) + 125) - 2000) * mult;

        var step = function () {
            var elapsed = Date.now() - clock;
            win.scroll(0, getY(start, end, elapsed, duration));

            if (elapsed <= duration) {
                animate(step);
            }
        };

        step();
    }

    function getY(start, end, elapsed, duration) {
        if (elapsed > duration) {
            return end;
        }

        return start + (end - start) * ease(elapsed / duration);
    }

    function ease(t) {
        return t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
    }

    // Export module
    return { ready: ready };
});
/**
 * ----------------------------------------------------------------------
 * Mica: Auto-select links to current page or section
 */
Mica.define('links', function ($, _) {
    //'use strict';

    var api = {};
    var $win = $(window);
    var designer;
    var inApp = Mica.env();
    var location = window.location;
    var linkCurrent = 'w--current';
    var validHash = /^#[a-zA-Z][\w:.-]*$/;
    var indexPage = /index\.(html|php)$/;
    var dirList = /\/$/;
    var anchors;
    var slug;

    // -----------------------------------
    // Module methods

    api.ready = api.design = api.preview = init;

    // -----------------------------------
    // Private methods

    function init() {
        designer = inApp && Mica.env('design');
        slug = Mica.env('slug') || location.pathname || '';

        // Reset scroll listener, init anchors
        Mica.scroll.off(scroll);
        anchors = [];

        // Test all links for a selectable href
        var links = document.links;
        for (var i = 0; i < links.length; ++i) {
            select(links[i]);
        }

        // Listen for scroll if any anchors exist
        if (anchors.length) {
            Mica.scroll.on(scroll);
            scroll();
        }
    }

    function select(link) {
        var href = link.getAttribute('href');

        // Ignore any hrefs with a colon to safely avoid all uri schemes
        if (href.indexOf(':') >= 0) return;

        var $link = $(link);

        // Check for valid hash links w/ sections and use scroll anchor
        if (href.indexOf('#') === 0 && validHash.test(href)) {
            // Ignore #edit anchors
            if (href === '#edit') return;
            var $section = $(href);
            $section.length && anchors.push({ link: $link, sec: $section, active: false });
            return;
        }

        // Ignore empty # links
        if (href === '#') return;

        // Determine whether the link should be selected
        var match = (link.href === location.href.split("?")[0].split("#")[0]) || (href === slug) || (indexPage.test(href) && dirList.test(slug));
        setClass($link, linkCurrent, match);
    }

    function scroll() {
        var viewTop = $win.scrollTop();
        var viewHeight = $win.height();

        // Check each anchor for a section in view
        _.each(anchors, function (anchor) {
            var $link = anchor.link;
            var $section = anchor.sec;
            var top = $section.offset().top;
            var height = $section.outerHeight();
            var offset = viewHeight * 0.5;
            var active = ($section.is(':visible') &&
              top + height - offset >= viewTop &&
              top + offset <= viewTop + viewHeight);
            if (anchor.active === active) return;
            anchor.active = active;
            setClass($link, linkCurrent, active);
            if (designer) $link[0].__wf_current = active;
        });
    }

    function setClass($elem, className, add) {
        var exists = $elem.hasClass(className);
        if (add && exists) return;
        if (!add && !exists) return;
        add ? $elem.addClass(className) : $elem.removeClass(className);
        if (add && $elem.closest('div').attr('class') == 'w-dropdown') $elem.closest('div').find('.w-dropdown-toggle').addClass(className);
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Slider component
 */
Mica.define('slider', function ($, _) {
    //'use strict';

    var api = {};
    var tram = window.tram;
    var $doc = $(document);
    var $sliders;
    var designer;
    var inApp = Mica.env();
    var namespace = '.w-slider';
    var dot = '<div class="w-slider-dot" data-wf-ignore />';
    var ix = Mica.ixEvents();
    var fallback;
    var redraw;

    // -----------------------------------
    // Module methods

    api.ready = function () {
        init();
    };

    api.design = function () {
        designer = true;
        init();
    };

    api.preview = function () {
        designer = false;
        init();
    };

    api.redraw = function () {
        redraw = true;
        init();
    };

    api.destroy = removeListeners;

    // -----------------------------------
    // Private methods

    function init() {
        // Find all sliders on the page
        $sliders = $doc.find(namespace);
        if (!$sliders.length) return;
        $sliders.filter(':visible').each(build);
        redraw = null;
        if (fallback) return;

        // Wire events
        removeListeners();
        addListeners();
    }

    function removeListeners() {
        Mica.resize.off(renderAll);
        Mica.redraw.off(api.redraw);
    }

    function addListeners() {
        Mica.resize.on(renderAll);
        Mica.redraw.on(api.redraw);
    }

    function renderAll() {
        $sliders.filter(':visible').each(render);
    }

    function build(i, el) {
        var $el = $(el);

        // Store slider state in data
        var data = $.data(el, namespace);
        if (!data) data = $.data(el, namespace, {
            index: 0,
            depth: 1,
            el: $el,
            config: {}
        });
        data.mask = $el.children('.w-slider-mask');
        data.left = $el.children('.w-slider-arrow-left');
        data.right = $el.children('.w-slider-arrow-right');
        data.nav = $el.children('.w-slider-nav');
        data.slides = data.mask.children('.w-slide');
        data.slides.each(ix.reset);
        if (redraw) data.maskWidth = 0;

        // Disable in old browsers
        if (!tram.support.transform) {
            data.left.hide();
            data.right.hide();
            data.nav.hide();
            fallback = true;
            return;
        }

        // Remove old events
        data.el.off(namespace);
        data.left.off(namespace);
        data.right.off(namespace);
        data.nav.off(namespace);

        // Set config from data attributes
        configure(data);

        // Add events based on mode
        if (designer) {
            data.el.on('setting' + namespace, handler(data));
            stopTimer(data);
            data.hasTimer = false;
        } else {
            data.el.on('swipe' + namespace, handler(data));
            data.left.on('tap' + namespace, previous(data));
            data.right.on('tap' + namespace, next(data));

            // Start timer if autoplay is true, only once
            if (data.config.autoplay && !data.hasTimer) {
                data.hasTimer = true;
                data.timerCount = 1;
                startTimer(data);
            }
        }

        // Listen to nav events
        data.nav.on('tap' + namespace, '> div', handler(data));

        // Remove gaps from formatted html (for inline-blocks)
        if (!inApp) {
            data.mask.contents().filter(function () {
                return this.nodeType === 3;
            }).remove();
        }

        // Run first render
        render(i, el);
    }

    function configure(data) {
        var config = {};

        config.crossOver = 0;

        // Set config options from data attributes
        config.animation = data.el.attr('data-animation') || 'slide';
        if (config.animation == 'outin') {
            config.animation = 'cross';
            config.crossOver = 0.5;
        }
        config.easing = data.el.attr('data-easing') || 'ease';

        var duration = data.el.attr('data-duration');
        config.duration = duration != null ? +duration : 500;

        if (+data.el.attr('data-infinite')) config.infinite = true;

        if (+data.el.attr('data-hide-arrows')) {
            config.hideArrows = true;
        } else if (data.config.hideArrows) {
            data.left.show();
            data.right.show();
        }

        if (+data.el.attr('data-autoplay')) {
            config.autoplay = true;
            config.delay = +data.el.attr('data-delay') || 2000;
            config.timerMax = +data.el.attr('data-autoplay-limit');
            // Disable timer on first touch or mouse down
            var touchEvents = 'mousedown' + namespace + ' touchstart' + namespace;
            if (!designer) data.el.off(touchEvents).one(touchEvents, function () {
                stopTimer(data);
            });
        }

        // Use edge buffer to help calculate page count
        var arrowWidth = data.right.width();
        config.edge = arrowWidth ? arrowWidth + 40 : 100;

        // Store config in data
        data.config = config;
    }

    function previous(data) {
        return function (evt) {
            change(data, { index: data.index - 1, vector: -1 });
        };
    }

    function next(data) {
        return function (evt) {
            change(data, { index: data.index + 1, vector: 1 });
        };
    }

    function select(data, value) {
        // Select page based on slide element index
        var found = null;
        if (value === data.slides.length) {
            init(); layout(data); // Rebuild and find new slides
        }
        _.each(data.anchors, function (anchor, index) {
            $(anchor.els).each(function (i, el) {
                if ($(el).index() === value) found = index;
            });
        });
        if (found != null) change(data, { index: found, immediate: true });
    }

    function startTimer(data) {
        stopTimer(data);
        var config = data.config;
        var timerMax = config.timerMax;
        if (timerMax && data.timerCount++ > timerMax) return;
        data.timerId = window.setTimeout(function () {
            if (data.timerId == null || designer) return;
            next(data)();
            startTimer(data);
        }, config.delay);
    }

    function stopTimer(data) {
        window.clearTimeout(data.timerId);
        data.timerId = null;
    }

    function handler(data) {
        return function (evt, options) {
            options = options || {};

            // Designer settings
            if (designer && evt.type == 'setting') {
                if (options.select == 'prev') return previous(data)();
                if (options.select == 'next') return next(data)();
                configure(data);
                layout(data);
                if (options.select == null) return;
                select(data, options.select);
                return;
            }

            // Swipe event
            if (evt.type == 'swipe') {
                if (Mica.env('editor')) return;
                if (options.direction == 'left') return next(data)();
                if (options.direction == 'right') return previous(data)();
                return;
            }

            // Page buttons
            if (data.nav.has(evt.target).length) {
                change(data, { index: $(evt.target).index() });
            }
        };
    }

    function change(data, options) {
        options = options || {};
        var config = data.config;
        var anchors = data.anchors;

        // Set new index
        data.previous = data.index;
        var index = options.index;
        var shift = {};
        if (index < 0) {
            index = anchors.length - 1;
            if (config.infinite) {
                // Shift first slide to the end
                shift.x = -data.endX;
                shift.from = 0;
                shift.to = anchors[0].width;
            }
        } else if (index >= anchors.length) {
            index = 0;
            if (config.infinite) {
                // Shift last slide to the start
                shift.x = anchors[anchors.length - 1].width;
                shift.from = -anchors[anchors.length - 1].x;
                shift.to = shift.from - shift.x;
            }
        }
        data.index = index;

        // Select page nav
        var active = data.nav.children().eq(data.index).addClass('w-active');
        data.nav.children().not(active).removeClass('w-active');

        // Hide arrows
        if (config.hideArrows) {
            data.index === anchors.length - 1 ? data.right.hide() : data.right.show();
            data.index === 0 ? data.left.hide() : data.left.show();
        }

        // Get page offset from anchors
        var lastOffsetX = data.offsetX || 0;
        var offsetX = data.offsetX = -anchors[data.index].x;
        var resetConfig = { x: offsetX, opacity: 1, visibility: '' };

        // Transition slides
        var targets = $(anchors[data.index].els);
        var previous = $(anchors[data.previous] && anchors[data.previous].els);
        var others = data.slides.not(targets);
        var animation = config.animation;
        var easing = config.easing;
        var duration = Math.round(config.duration);
        var vector = options.vector || (data.index > data.previous ? 1 : -1);
        var fadeRule = 'opacity ' + duration + 'ms ' + easing;
        var slideRule = 'transform ' + duration + 'ms ' + easing;

        // Trigger IX events
        if (!designer) {
            targets.each(ix.intro);
            others.each(ix.outro);
        }

        // Set immediately after layout changes (but not during redraw)
        if (options.immediate && !redraw) {
            tram(targets).set(resetConfig);
            resetOthers();
            return;
        }

        // Exit early if index is unchanged
        if (data.index == data.previous) return;

        // Cross Fade / Out-In
        if (animation == 'cross') {
            var reduced = Math.round(duration - duration * config.crossOver);
            var wait = Math.round(duration - reduced);
            fadeRule = 'opacity ' + reduced + 'ms ' + easing;
            tram(previous)
              .set({ visibility: '' })
              .add(fadeRule)
              .start({ opacity: 0 });
            tram(targets)
              .set({ visibility: '', x: offsetX, opacity: 0, zIndex: data.depth++ })
              .add(fadeRule)
              .wait(wait)
              .then({ opacity: 1 })
              .then(resetOthers);
            return;
        }

        // Fade Over
        if (animation == 'fade') {
            tram(previous)
              .set({ visibility: '' })
              .stop();
            tram(targets)
              .set({ visibility: '', x: offsetX, opacity: 0, zIndex: data.depth++ })
              .add(fadeRule)
              .start({ opacity: 1 })
              .then(resetOthers);
            return;
        }

        // Slide Over
        if (animation == 'over') {
            resetConfig = { x: data.endX };
            tram(previous)
              .set({ visibility: '' })
              .stop();
            tram(targets)
              .set({ visibility: '', zIndex: data.depth++, x: offsetX + anchors[data.index].width * vector })
              .add(slideRule)
              .start({ x: offsetX })
              .then(resetOthers);
            return;
        }

        // Slide - infinite scroll
        if (config.infinite && shift.x) {
            tram(data.slides.not(previous))
              .set({ visibility: '', x: shift.x })
              .add(slideRule)
              .start({ x: offsetX });
            tram(previous)
              .set({ visibility: '', x: shift.from })
              .add(slideRule)
              .start({ x: shift.to });
            data.shifted = previous;

        } else {
            if (config.infinite && data.shifted) {
                tram(data.shifted).set({ visibility: '', x: lastOffsetX });
                data.shifted = null;
            }

            // Slide - basic scroll
            tram(data.slides)
              .set({ visibility: '' })
              .add(slideRule)
              .start({ x: offsetX });
        }

        // Helper to move others out of view
        function resetOthers() {
            var targets = $(anchors[data.index].els);
            var others = data.slides.not(targets);
            if (animation != 'slide') resetConfig.visibility = 'hidden';
            tram(others).set(resetConfig);
        }
    }

    function render(i, el) {
        var data = $.data(el, namespace);
        if (maskChanged(data)) return layout(data);
        if (designer && slidesChanged(data)) layout(data);
    }

    function layout(data) {
        // Determine page count from width of slides
        var pages = 1;
        var offset = 0;
        var anchor = 0;
        var width = 0;
        var maskWidth = data.maskWidth;
        var threshold = maskWidth - data.config.edge;
        if (threshold < 0) threshold = 0;
        data.anchors = [{ els: [], x: 0, width: 0 }];
        data.slides.each(function (i, el) {
            if (anchor - offset > threshold) {
                pages++;
                offset += maskWidth;
                // Store page anchor for transition
                data.anchors[pages - 1] = { els: [], x: anchor, width: 0 };
            }
            // Set next anchor using current width + margin
            width = $(el).outerWidth(true);
            anchor += width;
            data.anchors[pages - 1].width += width;
            data.anchors[pages - 1].els.push(el);
        });
        data.endX = anchor;

        // Build dots if nav exists and needs updating
        if (designer) data.pages = null;
        if (data.nav.length && data.pages !== pages) {
            data.pages = pages;
            buildNav(data);
        }

        // Make sure index is still within range and call change handler
        var index = data.index;
        if (index >= pages) index = pages - 1;
        change(data, { immediate: true, index: index });
    }

    function buildNav(data) {
        var dots = [];
        var $dot;
        var spacing = data.el.attr('data-nav-spacing');
        if (spacing) spacing = parseFloat(spacing) + 'px';
        for (var i = 0; i < data.pages; i++) {
            $dot = $(dot);
            if (data.nav.hasClass('w-num')) $dot.text(i + 1);
            if (spacing != null) $dot.css({
                'margin-left': spacing,
                'margin-right': spacing
            });
            dots.push($dot);
        }
        data.nav.empty().append(dots);
    }

    function maskChanged(data) {
        var maskWidth = data.mask.width();
        if (data.maskWidth !== maskWidth) {
            data.maskWidth = maskWidth;
            return true;
        }
        return false;
    }

    function slidesChanged(data) {
        var slidesWidth = 0;
        data.slides.each(function (i, el) {
            slidesWidth += $(el).outerWidth(true);
        });
        if (data.slidesWidth !== slidesWidth) {
            data.slidesWidth = slidesWidth;
            return true;
        }
        return false;
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Lightbox component
 */
var lightbox = (function (window, document, $, tram, undefined) {
    //'use strict';

    var isArray = Array.isArray;
    var namespace = 'w-lightbox';
    var prefix = namespace + '-';
    var prefixRegex = /(^|\s+)/g;

    // Array of objects describing items to be displayed.
    var items = [];

    // Index of the currently displayed item.
    var currentIndex;

    // Object holding references to jQuery wrapped nodes.
    var $refs;

    // Instance of Spinner
    var spinner;

    function lightbox(thing, index) {
        items = isArray(thing) ? thing : [thing];

        if (!$refs) {
            lightbox.build();
        }

        if (items.length > 1) {
            $refs.items = $refs.empty;

            items.forEach(function (item) {
                var $thumbnail = dom('thumbnail');
                var $item = dom('item').append($thumbnail);

                $refs.items = $refs.items.add($item);

                loadImage(item.thumbnailUrl || item.url, function ($image) {
                    if ($image.prop('width') > $image.prop('height')) {
                        addClass($image, 'wide');
                    }
                    else {
                        addClass($image, 'tall');
                    }
                    $thumbnail.append(addClass($image, 'thumbnail-image'));
                });
            });

            $refs.strip.empty().append($refs.items);
            addClass($refs.content, 'group');
        }

        tram(
          // Focus the lightbox to receive keyboard events.
          removeClass($refs.lightbox, 'hide').focus()
        )
          .add('opacity .3s')
          .start({ opacity: 1 });

        // Prevent document from scrolling while lightbox is active.
        addClass($refs.html, 'noscroll');

        return lightbox.show(index || 0);
    }

    /**
     * Creates the DOM structure required by the lightbox.
     */
    lightbox.build = function () {
        // In case `build` is called more than once.
        lightbox.destroy();

        $refs = {
            html: $(document.documentElement),
            // Empty jQuery object can be used to build new ones using `.add`.
            empty: $()
        };

        $refs.arrowLeft = dom('control left inactive');
        $refs.arrowRight = dom('control right inactive');
        $refs.close = dom('control close');

        $refs.spinner = dom('spinner');
        $refs.strip = dom('strip');

        spinner = new Spinner($refs.spinner, prefixed('hide'));

        $refs.content = dom('content')
          .append($refs.spinner, $refs.arrowLeft, $refs.arrowRight, $refs.close);

        $refs.container = dom('container')
          .append($refs.content, $refs.strip);

        $refs.lightbox = dom('backdrop hide')
          .append($refs.container);

        // We are delegating events for performance reasons and also
        // to not have to reattach handlers when images change.
        $refs.strip.on('tap', selector('item'), itemTapHandler);
        $refs.content
          .on('swipe', swipeHandler)
          .on('tap', selector('left'), handlerPrev)
          .on('tap', selector('right'), handlerNext)
          .on('tap', selector('close'), handlerHide)
          .on('tap', selector('image, caption'), handlerNext);
        $refs.container
          .on('tap', selector('view, strip'), handlerHide)
          // Prevent images from being dragged around.
          .on('dragstart', selector('img'), preventDefault);
        $refs.lightbox
          .on('keydown', keyHandler)
          // IE loses focus to inner nodes without letting us know.
          .on('focusin', focusThis);

        // The `tabindex` attribute is needed to enable non-input elements
        // to receive keyboard events.
        $('body').append($refs.lightbox.prop('tabIndex', 0));

        return lightbox;
    };

    /**
     * Dispose of DOM nodes created by the lightbox.
     */
    lightbox.destroy = function () {
        if (!$refs) {
            return;
        }

        // Event handlers are also removed.
        $refs.lightbox.remove();
        $refs = undefined;
    };

    /**
     * Show a specific item.
     */
    lightbox.show = function (index) {
        // Bail if we are already showing this item.
        if (index === currentIndex) {
            return;
        }

        var item = items[index];
        var previousIndex = currentIndex;
        currentIndex = index;
        spinner.show();

        // For videos, load an empty SVG with the video dimensions to preserve
        // the video’s aspect ratio while being responsive.
        var url = item.html && svgDataUri(item.width, item.height) || item.url;
        loadImage(url, function ($image) {
            // Make sure this is the last item requested to be shown since
            // images can finish loading in a different order than they were
            // requested in.
            if (index != currentIndex) {
                return;
            }

            var $figure = dom('figure', 'figure').append(addClass($image, 'image'));
            var $frame = dom('frame').append($figure);
            var $newView = dom('view').append($frame);
            var $html, isIframe;

            if (item.html) {
                $html = $(item.html);
                isIframe = $html.is('iframe');

                if (isIframe) {
                    $html.on('load', transitionToNewView);
                }

                $figure.append(addClass($html, 'embed'));
            }

            if (item.caption) {
                $figure.append(dom('caption', 'figcaption').text(item.caption));
            }

            $refs.spinner.before($newView);

            if (!isIframe) {
                transitionToNewView();
            }

            function transitionToNewView() {
                spinner.hide();

                if (index != currentIndex) {
                    $newView.remove();
                    return;
                }


                toggleClass($refs.arrowLeft, 'inactive', index <= 0);
                toggleClass($refs.arrowRight, 'inactive', index >= items.length - 1);

                if ($refs.view) {
                    tram($refs.view)
                      .add('opacity .3s')
                      .start({ opacity: 0 })
                      .then(remover($refs.view));

                    tram($newView)
                      .add('opacity .3s')
                      .add('transform .3s')
                      .set({ x: index > previousIndex ? '80px' : '-80px' })
                      .start({ opacity: 1, x: 0 });
                }
                else {
                    $newView.css('opacity', 1);
                }

                $refs.view = $newView;

                if ($refs.items) {
                    // Mark proper thumbnail as active
                    addClass(removeClass($refs.items, 'active').eq(index), 'active');
                }
            }
        });

        return lightbox;
    };

    /**
     * Hides the lightbox.
     */
    lightbox.hide = function () {
        tram($refs.lightbox)
          .add('opacity .3s')
          .start({ opacity: 0 })
          .then(hideLightbox);

        return lightbox;
    };

    lightbox.prev = function () {
        if (currentIndex > 0) {
            lightbox.show(currentIndex - 1);
        }
    };

    lightbox.next = function () {
        if (currentIndex < items.length - 1) {
            lightbox.show(currentIndex + 1);
        }
    };

    function createHandler(action) {
        return function (event) {
            // We only care about events triggered directly on the bound selectors.
            if (this != event.target) {
                return;
            }

            event.stopPropagation();
            event.preventDefault();

            action();
        };
    }

    var handlerPrev = createHandler(lightbox.prev);
    var handlerNext = createHandler(lightbox.next);
    var handlerHide = createHandler(lightbox.hide);

    var itemTapHandler = function (event) {
        var index = $(this).index();

        event.preventDefault();
        lightbox.show(index);
    };

    var swipeHandler = function (event, data) {
        // Prevent scrolling.
        event.preventDefault();

        if (data.direction == 'left') {
            lightbox.next();
        }
        else if (data.direction == 'right') {
            lightbox.prev();
        }
    };

    var focusThis = function () {
        this.focus();
    };

    function preventDefault(event) {
        event.preventDefault();
    }

    function keyHandler(event) {
        var keyCode = event.keyCode;

        // [esc]
        if (keyCode == 27) {
            lightbox.hide();
        }

            // [◀]
        else if (keyCode == 37) {
            lightbox.prev();
        }

            // [▶]
        else if (keyCode == 39) {
            lightbox.next();
        }
    }

    function hideLightbox() {
        removeClass($refs.html, 'noscroll');
        addClass($refs.lightbox, 'hide');
        $refs.strip.empty();
        $refs.view && $refs.view.remove();

        // Reset some stuff
        removeClass($refs.content, 'group');
        addClass($refs.arrowLeft, 'inactive');
        addClass($refs.arrowRight, 'inactive');

        currentIndex = $refs.view = undefined;
    }

    function loadImage(url, callback) {
        var $image = dom('img', 'img');

        $image.one('load', function () {
            callback($image);
        });

        // Start loading image.
        $image.attr('src', url);

        return $image;
    }

    function remover($element) {
        return function () {
            $element.remove();
        };
    }

    /**
     * Spinner
     */
    function Spinner($spinner, className, delay) {
        this.$element = $spinner;
        this.className = className;
        this.delay = delay || 200;
        this.hide();
    }

    Spinner.prototype.show = function () {
        var spinner = this;

        // Bail if we are already showing the spinner.
        if (spinner.timeoutId) {
            return;
        }

        spinner.timeoutId = setTimeout(function () {
            spinner.$element.removeClass(spinner.className);
            delete spinner.timeoutId;
        }, spinner.delay);
    };

    Spinner.prototype.hide = function () {
        var spinner = this;
        if (spinner.timeoutId) {
            clearTimeout(spinner.timeoutId);
            delete spinner.timeoutId;
            return;
        }

        spinner.$element.addClass(spinner.className);
    };

    function prefixed(string, isSelector) {
        return string.replace(prefixRegex, (isSelector ? ' .' : ' ') + prefix);
    }

    function selector(string) {
        return prefixed(string, true);
    }

    /**
     * jQuery.addClass with auto-prefixing
     * @param  {jQuery} Element to add class to
     * @param  {string} Class name that will be prefixed and added to element
     * @return {jQuery}
     */
    function addClass($element, className) {
        return $element.addClass(prefixed(className));
    }

    /**
     * jQuery.removeClass with auto-prefixing
     * @param  {jQuery} Element to remove class from
     * @param  {string} Class name that will be prefixed and removed from element
     * @return {jQuery}
     */
    function removeClass($element, className) {
        return $element.removeClass(prefixed(className));
    }

    /**
     * jQuery.toggleClass with auto-prefixing
     * @param  {jQuery}  Element where class will be toggled
     * @param  {string}  Class name that will be prefixed and toggled
     * @param  {boolean} Optional boolean that determines if class will be added or removed
     * @return {jQuery}
     */
    function toggleClass($element, className, shouldAdd) {
        return $element.toggleClass(prefixed(className), shouldAdd);
    }

    /**
     * Create a new DOM element wrapped in a jQuery object,
     * decorated with our custom methods.
     * @param  {string} className
     * @param  {string} [tag]
     * @return {jQuery}
     */
    function dom(className, tag) {
        return addClass($(document.createElement(tag || 'div')), className);
    }

    function isObject(value) {
        return typeof value == 'object' && null != value && !isArray(value);
    }

    function svgDataUri(width, height) {
        var svg = '<svg xmlns="http://www.w3.org/2000/svg" width="' + width + '" height="' + height + '"/>';
        return 'data:image/svg+xml;charset=utf-8,' + encodeURI(svg);
    }

    // Compute some dimensions manually for iOS, because of buggy support for VH.
    // Also, Android built-in browser does not support viewport units.
    (function () {
        var ua = window.navigator.userAgent;
        var iOS = /(iPhone|iPod|iPad).+AppleWebKit/i.test(ua);
        var android = ua.indexOf('Android ') > -1 && ua.indexOf('Chrome') == -1;

        if (!iOS && !android) {
            return;
        }

        var styleNode = document.createElement('style');
        document.head.appendChild(styleNode);
        window.addEventListener('orientationchange', refresh, true);

        function refresh() {
            var vh = window.innerHeight;
            var vw = window.innerWidth;
            var content =
              '.w-lightbox-content, .w-lightbox-view, .w-lightbox-view:before {' +
                'height:' + vh + 'px' +
              '}' +
              '.w-lightbox-view {' +
                'width:' + vw + 'px' +
              '}' +
              '.w-lightbox-group, .w-lightbox-group .w-lightbox-view, .w-lightbox-group .w-lightbox-view:before {' +
                'height:' + (0.86 * vh) + 'px' +
              '}' +
              '.w-lightbox-image {' +
                'max-width:' + vw + 'px;' +
                'max-height:' + vh + 'px' +
              '}' +
              '.w-lightbox-group .w-lightbox-image {' +
                'max-height:' + (0.86 * vh) + 'px' +
              '}' +
              '.w-lightbox-strip {' +
                'padding: 0 ' + (0.01 * vh) + 'px' +
              '}' +
              '.w-lightbox-item {' +
                'width:' + (0.1 * vh) + 'px;' +
                'padding:' + (0.02 * vh) + 'px ' + (0.01 * vh) + 'px' +
              '}' +
              '.w-lightbox-thumbnail {' +
                'height:' + (0.1 * vh) + 'px' +
              '}' +
              '@media (min-width: 768px) {' +
                '.w-lightbox-content, .w-lightbox-view, .w-lightbox-view:before {' +
                  'height:' + (0.96 * vh) + 'px' +
                '}' +
                '.w-lightbox-content {' +
                  'margin-top:' + (0.02 * vh) + 'px' +
                '}' +
                '.w-lightbox-group, .w-lightbox-group .w-lightbox-view, .w-lightbox-group .w-lightbox-view:before {' +
                  'height:' + (0.84 * vh) + 'px' +
                '}' +
                '.w-lightbox-image {' +
                  'max-width:' + (0.96 * vw) + 'px;' +
                  'max-height:' + (0.96 * vh) + 'px' +
                '}' +
                '.w-lightbox-group .w-lightbox-image {' +
                  'max-width:' + (0.823 * vw) + 'px;' +
                  'max-height:' + (0.84 * vh) + 'px' +
                '}' +
              '}';

            styleNode.textContent = content;
        }

        refresh();
    })();

    return lightbox;
})(window, document, jQuery, window.tram);

Mica.define('lightbox', function ($, _) {
    //'use strict';

    var api = {};
    var $doc = $(document);
    var $body;
    var $lightboxes;
    var designer;
    var inApp = Mica.env();
    var namespace = '.w-lightbox';
    var groups;

    // -----------------------------------
    // Module methods

    api.ready = api.design = api.preview = init;

    // -----------------------------------
    // Private methods

    function init() {
        designer = inApp && Mica.env('design');
        $body = $(document.body);

        // Reset Lightbox
        lightbox.destroy();

        // Reset groups
        groups = {};

        // Find all instances on the page
        $lightboxes = $doc.find(namespace);
        $lightboxes.each(build);
    }

    function build(i, el) {
        var $el = $(el);

        // Store state in data
        var data = $.data(el, namespace);
        if (!data) data = $.data(el, namespace, {
            el: $el,
            mode: 'images',
            images: [],
            embed: ''
        });

        // Remove old events
        data.el.off(namespace);

        // Set config from json script tag
        configure(data);

        // Add events based on mode
        if (designer) {
            data.el.on('setting' + namespace, configure.bind(null, data));
        }
        else {
            data.el
              .on('tap' + namespace, tapHandler(data))
              // Prevent page scrolling to top when clicking on lightbox triggers.
              .on('click' + namespace, function (e) { e.preventDefault(); });
        }
    }

    function configure(data) {
        var json = data.el.children('.w-json').html();
        var groupName, groupItems;

        if (!json) {
            data.items = [];
            return;
        }

        try {
            json = JSON.parse(json);

            supportOldLightboxJson(json);

            groupName = json.group;

            if (groupName) {
                groupItems = groups[groupName];
                if (!groupItems) {
                    groupItems = groups[groupName] = [];
                }

                data.items = groupItems;

                if (json.items.length) {
                    data.index = groupItems.length;
                    groupItems.push.apply(groupItems, json.items);
                }
            }
            else {
                data.items = json.items;
            }
        }
        catch (e) {
            console.error('Malformed lightbox JSON configuration.', e.message);
        }
    }

    function tapHandler(data) {
        return function () {
            data.items.length && lightbox(data.items, data.index || 0);
        };
    }

    function supportOldLightboxJson(data) {
        if (data.images) {
            data.images.forEach(function (item) {
                item.type = 'image';
            });
            data.items = data.images;
        }

        if (data.embed) {
            data.embed.type = 'video';
            data.items = [data.embed];
        }

        if (data.groupId) {
            data.group = data.groupId;
        }
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Navbar component
 */
Mica.define('navbar', function ($, _) {
    //'use strict';

    var api = {};
    var tram = window.tram;
    var $win = $(window);
    var $doc = $(document);
    var $body;
    var $navbars;
    var designer;
    var inApp = Mica.env();
    var overlay = '<div class="w-nav-overlay" data-wf-ignore />';
    var namespace = '.w-nav';
    //var buttonOpen = 'w--open';
    var buttonOpen = 'is-show';
    var menuOpen = 'w--nav-menu-open';
    var linkOpen = 'w--nav-link-open';
    var ix = Mica.ixEvents();

    // -----------------------------------
    // Module methods

    api.ready = api.design = api.preview = init;
    api.destroy = removeListeners;

    // -----------------------------------
    // Private methods

    function init() {
        designer = inApp && Mica.env('design');
        $body = $(document.body);

        // Find all instances on the page
        $navbars = $doc.find(namespace);
        if (!$navbars.length) return;
        $navbars.each(build);

        // Wire events
        removeListeners();
        addListeners();
    }

    function removeListeners() {
        Mica.resize.off(resizeAll);
    }

    function addListeners() {
        Mica.resize.on(resizeAll);
    }

    function resizeAll() {
        $navbars.each(resize);
    }

    function build(i, el) {
        var $el = $(el);

        // Store state in data
        var data = $.data(el, namespace);
        if (!data) data = $.data(el, namespace, { open: false, el: $el, config: {} });
        data.menu = $el.find('.w-nav-menu');
        data.links = data.menu.find('.w-nav-link');
        data.dropdowns = data.menu.find('.w-dropdown');
        data.button = $el.find('.w-nav-button');
        data.container = $el.find('.w-container');
        data.outside = outside(data);

        // Remove old events
        data.el.off(namespace);
        data.button.off(namespace);
        data.menu.off(namespace);

        // Set config from data attributes
        configure(data);

        // Add events based on mode
        if (designer) {
            removeOverlay(data);
            data.el.on('setting' + namespace, handler(data));
        } else {
            addOverlay(data);
            data.button.on('tap' + namespace, toggle(data));
            data.menu.on('click' + namespace, 'a', navigate(data));
        }

        // Trigger initial resize
        resize(i, el);
    }

    function removeOverlay(data) {
        if (!data.overlay) return;
        close(data, true);
        data.overlay.remove();
        data.overlay = null;
    }

    function addOverlay(data) {
        if (data.overlay) return;
        data.overlay = $(overlay).appendTo(data.el);
        data.parent = data.menu.parent();
        close(data, true);
    }

    function configure(data) {
        var config = {};
        var old = data.config || {};

        // Set config options from data attributes
        var animation = config.animation = data.el.attr('data-animation') || 'default';
        config.animOver = /^over/.test(animation);
        config.animDirect = /left$/.test(animation) ? -1 : 1;

        // Re-open menu if the animation type changed
        if (old.animation != animation) {
            data.open && _.defer(reopen, data);
        }

        config.easing = data.el.attr('data-easing') || 'ease';
        config.easing2 = data.el.attr('data-easing2') || 'ease';

        var duration = data.el.attr('data-duration');
        config.duration = duration != null ? +duration : 400;

        config.docHeight = data.el.attr('data-doc-height');

        // Store config in data
        data.config = config;
    }

    function handler(data) {
        return function (evt, options) {
            options = options || {};
            var winWidth = $win.width();
            configure(data);
            options.open === true && open(data, true);
            options.open === false && close(data, true);
            // Reopen if media query changed after setting
            data.open && _.defer(function () {
                if (winWidth != $win.width()) reopen(data);
            });
        };
    }

    function reopen(data) {
        if (!data.open) return;
        close(data, true);
        open(data, true);
    }

    function toggle(data) {
        // Debounce toggle to wait for accurate open state
        return _.debounce(function (evt) {
            data.open ? close(data) : open(data);
        });
    }

    function navigate(data) {
        return function (evt) {
            var link = $(this);
            var href = link.attr('href');

            // Avoid late clicks on touch devices
            if (!Mica.validClick(evt.currentTarget)) {
                evt.preventDefault();
                return;
            }

            // Close when navigating to an in-page anchor
            if (href && href.indexOf('#') === 0 && data.open) {
                close(data);
            }
        };
    }

    function outside(data) {
        // Unbind previous tap handler if it exists
        if (data.outside) $doc.off('tap' + namespace, data.outside);

        // Close menu when tapped outside, debounced to wait for state
        return _.debounce(function (evt) {
            if (!data.open) return;
            var menu = $(evt.target).closest('.w-nav-menu');
            if (!data.menu.is(menu)) {
                close(data);
            }
        });
    }

    function resize(i, el) {
        var data = $.data(el, namespace);
        // Check for collapsed state based on button display
        var collapsed = data.collapsed = data.button.css('display') != 'none';
        // Close menu if button is no longer visible (and not in designer)
        if (data.open && !collapsed && !designer) close(data, true);
        // Set max-width of links + dropdowns to match container
        if (data.container.length) {
            var updateEachMax = updateMax(data);
            data.links.each(updateEachMax);
            data.dropdowns.each(updateEachMax);
        }
        // If currently open, update height to match body
        if (data.open) {
            setOverlayHeight(data);
        }
    }

    var maxWidth = 'max-width';
    function updateMax(data) {
        // Set max-width of each element to match container
        var containMax = data.container.css(maxWidth);
        if (containMax == 'none') containMax = '';
        return function (i, link) {
            link = $(link);
            link.css(maxWidth, '');
            // Don't set the max-width if an upstream value exists
            if (link.css(maxWidth) == 'none') link.css(maxWidth, containMax);
        };
    }

    function open(data, immediate) {
        if (data.open) return;
        data.open = true;
        data.menu.addClass(menuOpen);
        data.links.addClass(linkOpen);
        data.button.addClass(buttonOpen);
        var config = data.config;
        var animation = config.animation;
        if (animation == 'none' || !tram.support.transform) immediate = true;
        var bodyHeight = setOverlayHeight(data);
        var menuHeight = data.menu.outerHeight(true);
        var menuWidth = data.menu.outerWidth(true);
        var navHeight = data.el.height();
        var navbarEl = data.el[0];
        resize(0, navbarEl);
        ix.intro(0, navbarEl);
        Mica.redraw.up();

        // Listen for tap outside events
        if (!designer) $doc.on('tap' + namespace, data.outside);

        // No transition for immediate
        if (immediate) return;

        var transConfig = 'transform ' + config.duration + 'ms ' + config.easing;

        // Add menu to overlay
        if (data.overlay) {
            data.overlay.show().append(data.menu);
        }

        // Over left/right
        if (config.animOver) {
            tram(data.menu)
              .add(transConfig)
              .set({ x: config.animDirect * menuWidth, height: bodyHeight }).start({ x: 0 });
            data.overlay && data.overlay.width(menuWidth);
            return;
        }

        // Drop Down
        var offsetY = navHeight + menuHeight;
        tram(data.menu)
          .add(transConfig)
          .set({ y: -offsetY }).start({ y: 0 });
    }

    function setOverlayHeight(data) {
        var config = data.config;
        var bodyHeight = config.docHeight ? $doc.height() : $body.height();
        if (config.animOver) {
            data.menu.height(bodyHeight);
        } else if (data.el.css('position') != 'fixed') {
            bodyHeight -= data.el.height();
        }
        data.overlay && data.overlay.height(bodyHeight);
        return bodyHeight;
    }

    function close(data, immediate) {
        if (!data.open) return;
        data.open = false;
        data.button.removeClass(buttonOpen);
        var config = data.config;
        if (config.animation == 'none' || !tram.support.transform) immediate = true;
        var animation = config.animation;
        ix.outro(0, data.el[0]);

        // Stop listening for tap outside events
        $doc.off('tap' + namespace, data.outside);

        if (immediate) {
            tram(data.menu).stop();
            complete();
            return;
        }

        var transConfig = 'transform ' + config.duration + 'ms ' + config.easing2;
        var menuHeight = data.menu.outerHeight(true);
        var menuWidth = data.menu.outerWidth(true);
        var navHeight = data.el.height();

        // Over left/right
        if (config.animOver) {
            tram(data.menu)
              .add(transConfig)
              .start({ x: menuWidth * config.animDirect }).then(complete);
            return;
        }

        // Drop Down
        var offsetY = navHeight + menuHeight;
        tram(data.menu)
          .add(transConfig)
          .start({ y: -offsetY }).then(complete);

        function complete() {
            data.menu.height('');
            tram(data.menu).set({ x: 0, y: 0 });
            data.menu.removeClass(menuOpen);
            data.links.removeClass(linkOpen);
            if (data.overlay && data.overlay.children().length) {
                // Move menu back to parent
                data.menu.appendTo(data.parent);
                data.overlay.attr('style', '').hide();
            }

            // Trigger event so other components can hook in (dropdown)
            data.el.triggerHandler('w-close');
        }
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Dropdown component
 */
Mica.define('dropdown', function ($, _) {
    //'use strict';

    var api = {};
    var tram = window.tram;
    var $doc = $(document);
    var $dropdowns;
    var designer;
    var inApp = Mica.env();
    var namespace = '.w-dropdown';
    //var stateOpen = 'w--open';
    var stateOpen = 'is-show';
    var closeEvent = 'w-close' + namespace;
    var ix = Mica.ixEvents();

    // -----------------------------------
    // Module methods

    api.ready = api.design = api.preview = init;

    // -----------------------------------
    // Private methods

    function init() {
        designer = inApp && Mica.env('design');

        // Find all instances on the page
        $dropdowns = $doc.find(namespace);
        $dropdowns.each(build);
    }

    function build(i, el) {
        var $el = $(el);

        // Store state in data
        var data = $.data(el, namespace);
        if (!data) data = $.data(el, namespace, { open: false, el: $el, config: {} });
        data.list = $el.children('.w-dropdown-list');
        data.toggle = $el.children('.w-dropdown-toggle');
        data.links = data.list.children('.w-dropdown-link');
        data.outside = outside(data);
        data.complete = complete(data);

        // Remove old events
        $el.off(namespace);
        data.toggle.off(namespace);

        // Set config from data attributes
        configure(data);

        if (data.nav) data.nav.off(namespace);
        data.nav = $el.closest('.w-nav');
        data.nav.on(closeEvent, handler(data));

        // Add events based on mode
        if (designer) {
            $el.on('setting' + namespace, handler(data));
        } else {
            if ($(data.toggle).parents("ul.depth2").length < 1) {
                data.toggle.on('tap' + namespace, toggle(data));
                $el.on(closeEvent, handler(data));
            } else {
                //data.toggle.on('tap' + namespace, toggle(data));
                data.toggle.on("mouseover", function () {
                    if ($(window).width() < 1025) {
                        return;
                    }
                    open(data);
                });
                data.toggle.on("mouseout", function (e) {
                    //if ($(e.relatedTarget).parent().parent().parent()[0] != $(data.list)[0]) {
                    if ($(e.relatedTarget).parents("nav")[0] != $(data.list)[0]) {
                        if ($(window).width() < 1025) {
                            return;
                        }
                        close(data);
                    }
                });
                $(data.list[0]).children().on("mouseover", function (e) {
                    //e.stopPropagation();
                    if ($(window).width() < 1025) {
                        return;
                    }
                    open(data);
                });
                $(data.list[0]).children().on("mouseout", function (e) {
                    //e.stopPropagation();
                    if ($(window).width() < 1025) {
                        return;
                    }
                    close(data);
                });

                data.toggle.on("click", function () {
                    if ($(this).hasClass("is-show")) {
                        close(data);
                    } else {
                        open(data);
                    }
                });
                $(data.list[0]).children().on("click", function (e) {
                    //e.stopPropagation();
                    if ($(this).hasClass("is-show")) {
                        close(data);
                    } else {
                        open(data);
                    }
                });
                //data.toggle.hover(open(data),close(data));
            }
            // Close in preview mode
            inApp && close(data);
        }
    }

    function configure(data) {
        data.config = {
            hover: +data.el.attr('data-hover'),
            delay: +data.el.attr('data-delay') || 0
        };
    }

    function handler(data) {
        return function (evt, options) {
            options = options || {};

            if (evt.type == 'w-close') {
                return close(data);
            }

            if (evt.type == 'setting') {
                configure(data);
                options.open === true && open(data, true);
                options.open === false && close(data, true);
                return;
            }
        };
    }

    function toggle(data) {
        return _.debounce(function (evt) {
            data.open ? close(data) : open(data);
        });
    }

    function open(data, immediate) {
        if (data.open) return;
        closeOthers(data);
        data.open = true;
        data.list.addClass(stateOpen);
        data.toggle.addClass(stateOpen);
        ix.intro(0, data.el[0]);
        Mica.redraw.up();

        // Listen for tap outside events
        if (!designer) $doc.on('tap' + namespace, data.outside);

        // Clear previous delay
        window.clearTimeout(data.delayId);
    }

    function close(data, immediate) {
        if (!data.open) return;
        data.open = false;
        var config = data.config;
        ix.outro(0, data.el[0]);

        // Stop listening for tap outside events
        $doc.off('tap' + namespace, data.outside);

        // Clear previous delay
        window.clearTimeout(data.delayId);

        // Skip delay during immediate
        if (!config.delay || immediate) return data.complete();

        // Optionally wait for delay before close
        data.delayId = window.setTimeout(data.complete, config.delay);
    }

    function closeOthers(data) {
        var self = data.el[0];
        $dropdowns.each(function (i, other) {
            var $other = $(other);
            if ($other.is(self) || $other.has(self).length) return;
            $other.triggerHandler(closeEvent);
        });
    }

    function outside(data) {
        // Unbind previous tap handler if it exists
        if (data.outside) $doc.off('tap' + namespace, data.outside);

        // Close menu when tapped outside
        return _.debounce(function (evt) {
            if (!data.open) return;
            var $target = $(evt.target);
            if ($target.closest('.w-dropdown-toggle').length) return;
            if (!data.el.is($target.closest(namespace))) {
                close(data);
            }
        });
    }

    function complete(data) {
        return function () {
            data.list.removeClass(stateOpen);
            data.toggle.removeClass(stateOpen);
        };
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Tabs component
 */
Mica.define('tabs', function ($, _) {
    //'use strict';

    var api = {};
    var tram = window.tram;
    var $win = $(window);
    var $doc = $(document);
    var $tabs;
    var design;
    var env = Mica.env;
    var safari = env.safari;
    var inApp = env();
    var tabAttr = 'data-w-tab';
    var namespace = '.w-tabs';
    var linkCurrent = 'w--current';
    var tabActive = 'w--tab-active';
    var ix = Mica.ixEvents();

    // -----------------------------------
    // Module methods

    api.ready = api.design = api.preview = init;

    // -----------------------------------
    // Private methods

    function init() {
        design = inApp && Mica.env('design');

        // Find all instances on the page
        $tabs = $doc.find(namespace);
        if (!$tabs.length) return;
        $tabs.each(build);
    }

    function build(i, el) {
        var $el = $(el);

        // Store state in data
        var data = $.data(el, namespace);
        if (!data) data = $.data(el, namespace, { el: $el, config: {} });
        data.current = null;
        data.menu = $el.children('.w-tab-menu');
        data.links = data.menu.children('.w-tab-link');
        data.content = $el.children('.w-tab-content');
        data.panes = data.content.children('.w-tab-pane');

        // Remove old events
        data.el.off(namespace);
        data.links.off(namespace);

        // Set config from data attributes
        configure(data);

        // Wire up events when not in design mode
        if (!design) {
            data.links.on('click' + namespace, linkSelect(data));

            // Trigger first intro event from current tab
            var $link = data.links.filter('.' + linkCurrent);
            var tab = $link.attr(tabAttr);
            tab && changeTab(data, { tab: tab, immediate: true });
        }
    }

    function configure(data) {
        var config = {};
        var old = data.config || {};

        // Set config options from data attributes
        config.easing = data.el.attr('data-easing') || 'ease';

        var intro = +data.el.attr('data-duration-in');
        intro = config.intro = intro === intro ? intro : 0;

        var outro = +data.el.attr('data-duration-out');
        outro = config.outro = outro === outro ? outro : 0;

        config.immediate = !intro && !outro;

        // Store config in data
        data.config = config;
    }

    function linkSelect(data) {
        return function (evt) {
            var tab = evt.currentTarget.getAttribute(tabAttr);
            tab && changeTab(data, { tab: tab });
        };
    }

    function changeTab(data, options) {
        options = options || {};

        var config = data.config;
        var easing = config.easing;
        var tab = options.tab;

        // Don't select the same tab twice
        if (tab === data.current) return;
        data.current = tab;

        // Select the current link
        data.links.each(function (i, el) {
            var $el = $(el);
            if (el.getAttribute(tabAttr) === tab) $el.addClass(linkCurrent).each(ix.intro);
            else if ($el.hasClass(linkCurrent)) $el.removeClass(linkCurrent).each(ix.outro);
        });

        // Find the new tab panes and keep track of previous
        var targets = [];
        var previous = [];
        data.panes.each(function (i, el) {
            var $el = $(el);
            if (el.getAttribute(tabAttr) === tab) {
                targets.push(el);
            } else if ($el.hasClass(tabActive)) {
                previous.push(el);
            }
        });

        var $targets = $(targets);
        var $previous = $(previous);

        // Switch tabs immediately and bypass transitions
        if (options.immediate || config.immediate) {
            $targets.addClass(tabActive).each(ix.intro);
            $previous.removeClass(tabActive);
            Mica.redraw.up();
            return;
        }

        // Fade out the currently active tab before intro
        if ($previous.length && config.outro) {
            $previous.each(ix.outro);
            tram($previous)
              .add('opacity ' + config.outro + 'ms ' + easing, { fallback: safari })
              .start({ opacity: 0 })
              .then(intro);
        } else {
            // Skip the outro and play intro
            intro();
        }

        // Fade in the new target
        function intro() {
            // Clear previous active class + inline style
            $previous.removeClass(tabActive).removeAttr('style');

            // Add active class to new target
            $targets.addClass(tabActive).each(ix.intro);
            Mica.redraw.up();

            // Set opacity immediately if intro is zero
            if (!config.intro) return tram($targets).set({ opacity: 1 });

            // Otherwise fade in opacity
            tram($targets)
              .set({ opacity: 0 })
              .redraw()
              .add('opacity ' + config.intro + 'ms ' + easing, { fallback: safari })
              .start({ opacity: 1 });
        }
    }

    // Export module
    return api;
});
/**
 * ----------------------------------------------------------------------
 * Mica: Brand pages on the subdomain
 */
Mica.define('branding', function ($, _) {
    //'use strict';

    var api = {};
    var $html = $('html');
    var $body = $('body');
    var location = window.location;
    var inApp = Mica.env();

    // -----------------------------------
    // Module methods

    api.ready = function () {
        var doBranding = $html.attr("data-wf-status") && location.href.match(/mica.com|micatest.com/);

        if (doBranding) {
            var $branding = $('<div></div>');
            var $link = $('<a></a>');
            $link.attr('href', 'http://mica.com');

            $branding.css({
                position: 'fixed',
                bottom: 0,
                right: 0,
                borderTop: '5px solid #2b3239',
                borderLeft: '5px solid #2b3239',
                borderTopLeftRadius: '5px',
                backgroundColor: '#2b3239',
                padding: '5px 5px 5px 10px',
                fontFamily: 'Arial',
                fontSize: '10px',
                textTransform: 'uppercase'

            });

            $link.css({
                color: '#AAADB0',
                textDecoration: 'none'
            });

            var $micaLogo = $('<img>');
            $micaLogo.attr('src', ' ');
            $micaLogo.css({
                opacity: 0.9,
                width: '55px',
                verticalAlign: 'middle',
                paddingLeft: '4px',
                paddingBottom: '3px'
            });

            $branding.text('Built with');
            $branding.append($micaLogo);
            $link.append($branding);
            $body.append($link);
        }
    };

    // Export module
    return api;
});
